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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TranscriptPdfServiceIT extends FacadeIT {

  @Autowired private TranscriptPdfService transcriptPdfService;

  @Autowired private StudentRepository studentRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private CourseUnitRepository courseUnitRepository;

  @Autowired private CourseUnitCourseRepository courseUnitCourseRepository;

  @Autowired private ExamRepository examRepository;

  @Autowired private GradeRepository gradeRepository;

  @Autowired private TeacherRepository teacherRepository;

  @BeforeEach
  void setUp() {
    cleanDatabase();
  }

  @Test
  void buildTranscriptHtml_shouldIncludeStudentCourseUnitAndComputedAverages() {
    var student = createStudent();
    var semesterId = createSemester();

    var teacher = createTeacher();
    var course = createCourse("MTH101", "Algèbre linéaire");
    var exam = createExam(course, "100.00");
    createGrade(student, exam, teacher, "16.00");

    var courseUnit = createCourseUnit(semesterId, "UE-MATH", "Mathématiques", 6);
    linkCourseToUnit(courseUnit, course, 6);

    var html = transcriptPdfService.buildTranscriptHtml(student.getId(), semesterId);

    assertThat(html).startsWith("<html>").endsWith("</html>\n");
    assertThat(html).contains(student.getFirstName());
    assertThat(html).contains(student.getLastName());
    assertThat(html).contains(student.getMatricule());
    assertThat(html).contains("MTH101");
    assertThat(html).contains("Algèbre linéaire");
    assertThat(html).contains("UE-MATH");
    assertThat(html).contains("Mathématiques");
    assertThat(html).contains("16.0000");
  }

  @Test
  void buildTranscriptHtml_shouldNotFailWhenStudentHasNoGradesYet() {
    var student = createStudent();
    var semesterId = createSemester();
    createCourseUnit(semesterId, "UE-EMPTY", "Unité vide", 6);

    var html = transcriptPdfService.buildTranscriptHtml(student.getId(), semesterId);

    assertThat(html).isNotBlank();
    assertThat(html).contains(student.getMatricule());
    assertThat(html).contains("Moyenne générale du semestre : 0.0000 / 20");
  }

  @Test
  void buildTranscriptHtml_shouldThrowWhenStudentDoesNotExist() {
    var semesterId = createSemester();

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> transcriptPdfService.buildTranscriptHtml(UUID.randomUUID(), semesterId));
  }

  private JStudent createStudent() {
    return studentRepository.save(
        JStudent.builder()
            .firstName("Rindra")
            .lastName("Rakoto")
            .email("student-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .matricule(
                "STD26" + java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 999999))
            .build());
  }

  private UUID createSemester() {
    var cohortId = UUID.randomUUID();
    var academicYearId = UUID.randomUUID();
    var semesterId = UUID.randomUUID();

    jdbcTemplate().update("INSERT INTO cohort (id, entry_year) VALUES (?, ?)", cohortId, 2027);
    jdbcTemplate()
        .update(
            "INSERT INTO academic_year (id, name, start_year, end_year) VALUES (?, ?, ?, ?)",
            academicYearId,
            "AY-" + UUID.randomUUID().toString().substring(0, 6),
            2026,
            2027);
    jdbcTemplate()
        .update(
            "INSERT INTO semester (id, number, cohort_id, academic_year_id) VALUES (?, ?, ?, ?)",
            semesterId,
            1,
            cohortId,
            academicYearId);
    return semesterId;
  }

  private JCourse createCourse(String reference, String title) {
    return courseRepository.save(
        JCourse.builder()
            .reference(reference)
            .title(title)
            .coefficient(new BigDecimal("1.00"))
            .build());
  }

  private JExam createExam(JCourse course, String weighting) {
    return examRepository.save(
        JExam.builder()
            .courseId(course.getId())
            .type(JExamType.FINAL_EXAM)
            .examDate(LocalDateTime.now())
            .weighting(new BigDecimal(weighting))
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
            .matricule(
                "TCH" + java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 99999))
            .build());
  }

  private JGrade createGrade(JStudent student, JExam exam, JTeacher teacher, String value) {
    return gradeRepository.save(
        JGrade.builder()
            .studentMatricule(student.getMatricule())
            .examId(exam.getId())
            .value(new BigDecimal(value))
            .enteredAt(LocalDateTime.now())
            .teacherMatricule(teacher.getMatricule())
            .adminId(null)
            .build());
  }

  private JCourseUnit createCourseUnit(UUID semesterId, String code, String name, int credits) {
    return courseUnitRepository.save(
        JCourseUnit.builder()
            .code(code)
            .name(name)
            .credits(credits)
            .semesterId(semesterId)
            .build());
  }

  private void linkCourseToUnit(JCourseUnit courseUnit, JCourse course, int credits) {
    courseUnitCourseRepository.save(
        new JCourseUnitCourse(courseUnit.getId(), course.getId(), credits));
  }
}
