package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAdmin;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.entity.JGrade;
import com.example.demo.entity.JGroup;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTeacher;
import com.example.demo.entity.JTeachingAssignment;
import com.example.demo.model.Grade;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class GradeServiceIT extends FacadeIT {

  @Autowired private GradeService gradeService;

  @Autowired private GradeRepository gradeRepository;

  @Autowired private ExamRepository examRepository;

  @Autowired private CourseRepository courseRepository;

  @Autowired private StudentRepository studentRepository;

  @Autowired private TeacherRepository teacherRepository;

  @Autowired private AdminRepository adminRepository;

  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;

  @Autowired private DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    jdbcTemplate = new JdbcTemplate(dataSource);

    gradeRepository.deleteAll();
    teachingAssignmentRepository.deleteAll();
    examRepository.deleteAll();
    teacherRepository.deleteAll();
    studentRepository.deleteAll();
    adminRepository.deleteAll();
    courseRepository.deleteAll();

    jdbcTemplate.update("DELETE FROM \"group\"");
    jdbcTemplate.update("DELETE FROM cohort");
  }

  @Test
  void computeRetainedGrade_shouldComputeWeightedAverageWithoutRetake() {
    var student = createStudent();
    var course = createCourse();

    var ccExam = createExam(course, JExamType.CONTINUOUS_ASSESSMENT, "30.00");
    var finalExam = createExam(course, JExamType.FINAL_EXAM, "70.00");

    createTeacherGrade(student, ccExam, "10.00");
    createTeacherGrade(student, finalExam, "16.00");

    var result = gradeService.computeRetainedGrade(student.getMatricule(), course.getId());

    assertThat(result).isEqualByComparingTo("14.2000");
  }

  @Test
  void computeRetainedGrade_shouldKeepRetakeWhenHigherThanNormalTotal() {
    var student = createStudent();
    var course = createCourse();

    var finalExam = createExam(course, JExamType.FINAL_EXAM, "100.00");
    var retakeExam = createExam(course, JExamType.RETAKE, "100.00");

    createTeacherGrade(student, finalExam, "8.00");
    createTeacherGrade(student, retakeExam, "13.00");

    var result = gradeService.computeRetainedGrade(student.getMatricule(), course.getId());

    assertThat(result).isEqualByComparingTo("13.00");
  }

  @Test
  void computeRetainedGrade_shouldKeepNormalTotalWhenRetakeGradeDoesNotExist() {
    var student = createStudent();
    var course = createCourse();

    var finalExam = createExam(course, JExamType.FINAL_EXAM, "100.00");

    createExam(course, JExamType.RETAKE, "100.00");

    createTeacherGrade(student, finalExam, "15.00");

    var result = gradeService.computeRetainedGrade(student.getMatricule(), course.getId());

    assertThat(result).isEqualByComparingTo("15.0000");
  }

  @Test
  void computeRetainedGrade_shouldKeepNormalTotalWhenRetakeIsLower() {
    var student = createStudent();
    var course = createCourse();

    var finalExam = createExam(course, JExamType.FINAL_EXAM, "100.00");
    var retakeExam = createExam(course, JExamType.RETAKE, "100.00");

    createTeacherGrade(student, finalExam, "15.00");
    createTeacherGrade(student, retakeExam, "10.00");

    var result = gradeService.computeRetainedGrade(student.getMatricule(), course.getId());

    assertThat(result).isEqualByComparingTo("15.0000");
  }

  @Test
  void computeRetainedGrade_shouldReturnZeroWhenStudentHasNoGrades() {
    var student = createStudent();
    var course = createCourse();

    createExam(course, JExamType.FINAL_EXAM, "100.00");

    var result = gradeService.computeRetainedGrade(student.getMatricule(), course.getId());

    assertThat(result).isEqualByComparingTo("0");
  }

  @Test
  void computeOverallGrade_shouldCalculateWeightedAverageAcrossCourses() {
    var student = createStudent();

    var mathematics = createCourse("Mathematics", "2.00");
    var computerScience = createCourse("Computer Science", "3.00");
    var english = createCourse("English", "1.00");

    var mathematicsExam = createExam(mathematics, JExamType.FINAL_EXAM, "100.00");

    var computerScienceExam = createExam(computerScience, JExamType.FINAL_EXAM, "100.00");

    var englishExam = createExam(english, JExamType.FINAL_EXAM, "100.00");

    createTeacherGrade(student, mathematicsExam, "14.00");
    createTeacherGrade(student, computerScienceExam, "16.00");
    createTeacherGrade(student, englishExam, "12.00");

    var result = gradeService.computeOverallGrade(student.getMatricule());

    assertThat(result).isEqualByComparingTo("14.6667");
  }

  @Test
  void computeOverallGrade_shouldReturnZeroWhenStudentHasNoGrades() {
    var student = createStudent();

    var result = gradeService.computeOverallGrade(student.getMatricule());

    assertThat(result).isEqualByComparingTo("0");
  }

  @Test
  void getGradesForStudent_shouldReturnStudentGrades() {
    var student = createStudent();
    var course = createCourse();
    var exam = createExam(course, JExamType.FINAL_EXAM, "100.00");

    var teacher = createTeacher();

    createGroup();

    createTeacherGrade(student, exam, "15.00", teacher);

    var result = gradeService.getGradesForStudent(student.getMatricule());

    assertThat(result).hasSize(1);

    assertThat(result.getFirst().studentMatricule()).isEqualTo(student.getMatricule());

    assertThat(result.getFirst().examId()).isEqualTo(exam.getId());

    assertThat(result.getFirst().value()).isEqualByComparingTo("15.00");

    assertThat(result.getFirst().teacherMatricule()).isEqualTo(teacher.getMatricule());
  }

  @Test
  void createGradeByTeacher_shouldCreateGradeWithTeacherAsAuthor() {
    var student = createStudent();
    var course = createCourse();

    var exam = createExam(course, JExamType.FINAL_EXAM, "100.00");

    var teacher = createTeacher();

    createGroupAndAssignment(teacher, course);

    var grade =
        new Grade(
            null,
            student.getMatricule(),
            exam.getId(),
            new BigDecimal("14.50"),
            null,
            null,
            null,
            null);

    var result = gradeService.createGradeByTeacher(grade, teacher.getId());

    assertThat(result.id()).isNotNull();

    assertThat(result.studentMatricule()).isEqualTo(student.getMatricule());

    assertThat(result.examId()).isEqualTo(exam.getId());

    assertThat(result.value()).isEqualByComparingTo("14.50");

    assertThat(result.teacherMatricule()).isEqualTo(teacher.getMatricule());

    assertThat(result.adminId()).isNull();

    assertThat(result.enteredAt()).isNotNull();

    var saved = gradeRepository.findById(result.id()).orElseThrow();

    assertThat(saved.getStudentMatricule()).isEqualTo(student.getMatricule());

    assertThat(saved.getTeacherMatricule()).isEqualTo(teacher.getMatricule());

    assertThat(saved.getAdminId()).isNull();
  }

  @Test
  void createGradeByAdmin_shouldCreateGradeWithAdminAsAuthor() {
    var student = createStudent();
    var course = createCourse();

    var exam = createExam(course, JExamType.FINAL_EXAM, "100.00");

    var admin = createAdmin();

    var grade =
        new Grade(
            null,
            student.getMatricule(),
            exam.getId(),
            new BigDecimal("17.00"),
            null,
            null,
            null,
            null);

    var result = gradeService.createGradeByAdmin(grade, admin.getId());

    assertThat(result.id()).isNotNull();

    assertThat(result.studentMatricule()).isEqualTo(student.getMatricule());

    assertThat(result.examId()).isEqualTo(exam.getId());

    assertThat(result.value()).isEqualByComparingTo("17.00");

    assertThat(result.teacherMatricule()).isNull();

    assertThat(result.adminId()).isEqualTo(admin.getId());

    assertThat(result.enteredAt()).isNotNull();

    var saved = gradeRepository.findById(result.id()).orElseThrow();

    assertThat(saved.getStudentMatricule()).isEqualTo(student.getMatricule());

    assertThat(saved.getAdminId()).isEqualTo(admin.getId());

    assertThat(saved.getTeacherMatricule()).isNull();
  }

  private JStudent createStudent() {
    return studentRepository.save(
        JStudent.builder()
            .firstName("Student")
            .lastName("Test")
            .email("student-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .matricule(generateStudentMatricule())
            .build());
  }

  private String generateStudentMatricule() {
    int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

    return "STD26" + randomNumber;
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

  private JAdmin createAdmin() {
    return adminRepository.save(
        JAdmin.builder()
            .firstName("Admin")
            .lastName("Test")
            .email("admin-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .address("Antananarivo")
            .build());
  }

  private JCourse createCourse() {
    return createCourse("Test Course", "1.00");
  }

  private JCourse createCourse(String title, String coefficient) {

    return courseRepository.save(
        JCourse.builder()
            .reference("COURSE-" + UUID.randomUUID().toString().substring(0, 8))
            .title(title)
            .coefficient(new BigDecimal(coefficient))
            .build());
  }

  private JExam createExam(JCourse course, JExamType type, String weighting) {

    return examRepository.save(
        JExam.builder()
            .courseId(course.getId())
            .type(type)
            .examDate(LocalDateTime.now())
            .weighting(new BigDecimal(weighting))
            .build());
  }

  private JGrade createTeacherGrade(JStudent student, JExam exam, String value) {

    return createTeacherGrade(student, exam, value, createTeacher());
  }

  private JGrade createTeacherGrade(JStudent student, JExam exam, String value, JTeacher teacher) {

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

  private JGroup createGroup() {
    var cohortId = UUID.randomUUID();

    jdbcTemplate.update("INSERT INTO cohort (id, entry_year) VALUES (?, ?)", cohortId, 2026);

    return new JGroup();
  }

  private JTeachingAssignment createGroupAndAssignment(JTeacher teacher, JCourse course) {

    var cohortId = UUID.randomUUID();
    var groupId = UUID.randomUUID();

    jdbcTemplate.update("INSERT INTO cohort (id, entry_year) VALUES (?, ?)", cohortId, 2026);

    jdbcTemplate.update(
        "INSERT INTO \"group\" (id, reference, cohort_id) VALUES (?, ?, ?)",
        groupId,
        "GRP-" + UUID.randomUUID().toString().substring(0, 8),
        cohortId);

    return teachingAssignmentRepository.save(
        JTeachingAssignment.builder()
            .teacherId(teacher.getId())
            .courseId(course.getId())
            .groupId(groupId)
            .build());
  }
}
