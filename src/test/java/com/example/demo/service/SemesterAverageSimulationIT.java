package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JCourseUnit;
import com.example.demo.entity.JCourseUnitCourse;
import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.entity.JGrade;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTeacher;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
class SemesterAverageSimulationIT extends FacadeIT {

  @Autowired private GradeService gradeService;
  @Autowired private GradeRepository gradeRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private CourseUnitRepository courseUnitRepository;
  @Autowired private CourseUnitCourseRepository courseUnitCourseRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    jdbcTemplate = new JdbcTemplate(dataSource);

    jdbcTemplate.update("DELETE FROM course_unit_course");
    courseUnitRepository.deleteAll();
    jdbcTemplate.update("DELETE FROM semester");
    jdbcTemplate.update("DELETE FROM academic_year");
    gradeRepository.deleteAll();
    examRepository.deleteAll();
    teacherRepository.deleteAll();
    studentRepository.deleteAll();
    courseRepository.deleteAll();
    jdbcTemplate.update("DELETE FROM \"group\"");
    jdbcTemplate.update("DELETE FROM cohort");
  }

  @Test
  void simulateStudentGrades_andComputeSemesterAverage() {
    var student = createStudent("Herimamy", "Fenohasina");
    var semesterId = createSemester();

    // Course unit 1: "Programming" (10 credits) — two courses
    var unitProgramming = createCourseUnit(semesterId, "UE-PROG", "Programming", 10);
    var java = createCourse("PROG101", "Java Fundamentals");
    var web = createCourse("PROG102", "Web Development");
    linkCourseToUnit(unitProgramming, java, 6);
    linkCourseToUnit(unitProgramming, web, 4);
    gradeCourse(student, java, "15.50");
    gradeCourse(student, web, "12.00");

    var unitMath = createCourseUnit(semesterId, "UE-MATH", "Mathematics", 8);
    var algebra = createCourse("MATH201", "Linear Algebra");
    linkCourseToUnit(unitMath, algebra, 8);
    gradeCourse(student, algebra, "9.00");

    var unitEnglish = createCourseUnit(semesterId, "UE-ENG", "English", 4);
    var english = createCourse("ENG301", "Technical English");
    linkCourseToUnit(unitEnglish, english, 4);
    gradeCourse(student, english, "17.00");

    var programmingAverage =
        gradeService.computeCourseUnitAverage(student.getMatricule(), unitProgramming);
    var mathAverage = gradeService.computeCourseUnitAverage(student.getMatricule(), unitMath);
    var englishAverage = gradeService.computeCourseUnitAverage(student.getMatricule(), unitEnglish);
    var semesterAverage = gradeService.computeSemesterAverage(student.getMatricule(), semesterId);

    log.info(
        "=== Simulation results for {} ({}) ===", student.getFirstName(), student.getMatricule());
    log.info("UE-PROG (Programming, 10 credits): {} / 20", programmingAverage);
    log.info("UE-MATH (Mathematics, 8 credits): {} / 20", mathAverage);
    log.info("UE-ENG (English, 4 credits): {} / 20", englishAverage);
    log.info("Semester average: {} / 20", semesterAverage);

    assertThat(programmingAverage).isEqualByComparingTo("14.1000");
    assertThat(mathAverage).isEqualByComparingTo("9.0000");
    assertThat(englishAverage).isEqualByComparingTo("17.0000");
    assertThat(semesterAverage).isEqualByComparingTo("12.7727");
  }

  private JStudent createStudent(String firstName, String lastName) {
    return studentRepository.save(
        JStudent.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(firstName.toLowerCase() + "-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .matricule("STD26" + ThreadLocalRandom.current().nextInt(100000, 1000000))
            .build());
  }

  private JCourse createCourse(String reference, String title) {
    return courseRepository.save(
        JCourse.builder()
            .reference(reference + "-" + UUID.randomUUID().toString().substring(0, 4))
            .title(title)
            .coefficient(new BigDecimal("1.00"))
            .build());
  }

  private void gradeCourse(JStudent student, JCourse course, String value) {
    var exam =
        examRepository.save(
            JExam.builder()
                .courseId(course.getId())
                .type(JExamType.FINAL_EXAM)
                .examDate(LocalDateTime.now())
                .weighting(new BigDecimal("100.00"))
                .build());

    var teacher = createTeacher();

    gradeRepository.save(
        JGrade.builder()
            .studentMatricule(student.getMatricule())
            .examId(exam.getId())
            .value(new BigDecimal(value))
            .enteredAt(LocalDateTime.now())
            .teacherMatricule(teacher.getMatricule())
            .adminId(null)
            .build());
  }

  private JTeacher createTeacher() {
    return teacherRepository.save(
        JTeacher.builder()
            .firstName("Teacher")
            .lastName("Test")
            .email("teacher-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .address("Antananarivo")
            .matricule("TCH" + ThreadLocalRandom.current().nextInt(10000, 100000))
            .build());
  }

  private UUID createSemester() {
    var cohortId = UUID.randomUUID();
    var academicYearId = UUID.randomUUID();
    var semesterId = UUID.randomUUID();

    jdbcTemplate.update("INSERT INTO cohort (id, entry_year) VALUES (?, ?)", cohortId, 2027);

    jdbcTemplate.update(
        "INSERT INTO academic_year (id, name, start_year, end_year) VALUES (?, ?, ?, ?)",
        academicYearId,
        "AY-" + UUID.randomUUID().toString().substring(0, 6),
        2026,
        2027);

    jdbcTemplate.update(
        "INSERT INTO semester (id, number, cohort_id, academic_year_id) VALUES (?, ?, ?, ?)",
        semesterId,
        1,
        cohortId,
        academicYearId);

    return semesterId;
  }

  private UUID createCourseUnit(UUID semesterId, String code, String name, int credits) {
    return courseUnitRepository
        .save(
            JCourseUnit.builder()
                .code(code + "-" + UUID.randomUUID().toString().substring(0, 4))
                .name(name)
                .credits(credits)
                .semesterId(semesterId)
                .build())
        .getId();
  }

  private void linkCourseToUnit(UUID courseUnitId, JCourse course, int credits) {
    courseUnitCourseRepository.save(new JCourseUnitCourse(courseUnitId, course.getId(), credits));
  }
}
