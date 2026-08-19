package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.entity.JGrade;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTeacher;
import com.example.demo.entity.JTeachingAssignment;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Covers {@code GradeService.getStudentsMissingGradeForExam}, which powers the "missing grades"
 * teacher dashboard. Uses raw JDBC for group/group_membership like the rest of the grade IT suite,
 * since those domains don't have their own entities wired into this module yet.
 */
class GradeServiceMissingGradesIT extends FacadeIT {

  @Autowired private GradeService gradeService;
  @Autowired private StudentRepository studentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private GradeRepository gradeRepository;
  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;
  @Autowired private DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.update("DELETE FROM group_membership");
    gradeRepository.deleteAll();
    teachingAssignmentRepository.deleteAll();
    examRepository.deleteAll();
    teacherRepository.deleteAll();
    studentRepository.deleteAll();
    courseRepository.deleteAll();
    jdbcTemplate.update("DELETE FROM \"group\"");
    jdbcTemplate.update("DELETE FROM cohort");
  }

  @Test
  void returns_only_active_group_members_without_a_grade_for_the_exam() {
    var teacher = createTeacher();
    var course = createCourse();
    var exam = createExam(course);

    UUID groupId = createGroupAndAssignment(teacher, course).getGroupId();

    var studentWithGrade = createStudent();
    var studentWithoutGrade = createStudent();
    var studentNoLongerInGroup = createStudent();

    addActiveMembership(studentWithGrade.getId(), groupId);
    addActiveMembership(studentWithoutGrade.getId(), groupId);
    addClosedMembership(studentNoLongerInGroup.getId(), groupId);

    gradeRepository.save(
        JGrade.builder()
            .studentMatricule(studentWithGrade.getMatricule())
            .examId(exam.getId())
            .value(new BigDecimal("15.00"))
            .enteredAt(LocalDateTime.now())
            .teacherMatricule(teacher.getMatricule())
            .adminId(null)
            .build());

    var missing = gradeService.getStudentsMissingGradeForExam(teacher.getId(), exam.getId());

    assertThat(missing).hasSize(1);
    assertThat(missing.get(0).studentMatricule()).isEqualTo(studentWithoutGrade.getMatricule());
  }

  private JStudent createStudent() {
    return studentRepository.save(
        JStudent.builder()
            .firstName("Student")
            .lastName("Test")
            .email("student-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .address("Antananarivo")
            .matricule("STD26" + ThreadLocalRandom.current().nextInt(100000, 1000000))
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

  private JCourse createCourse() {
    return courseRepository.save(
        JCourse.builder()
            .reference("COURSE-" + UUID.randomUUID().toString().substring(0, 8))
            .title("Missing grades test course")
            .coefficient(new BigDecimal("1.00"))
            .build());
  }

  private JExam createExam(JCourse course) {
    return examRepository.save(
        JExam.builder()
            .courseId(course.getId())
            .type(JExamType.FINAL_EXAM)
            .examDate(LocalDateTime.now())
            .weighting(new BigDecimal("100.00"))
            .build());
  }

  private JTeachingAssignment createGroupAndAssignment(JTeacher teacher, JCourse course) {
    var cohortId = UUID.randomUUID();
    var groupId = UUID.randomUUID();

    jdbcTemplate.update("INSERT INTO cohort (id, entry_year) VALUES (?, ?)", cohortId, 2028);
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

  private void addActiveMembership(UUID studentId, UUID groupId) {
    jdbcTemplate.update(
        "INSERT INTO group_membership (id, student_id, group_id, start_date, end_date) VALUES"
            + " (?, ?, ?, ?, NULL)",
        UUID.randomUUID(),
        studentId,
        groupId,
        LocalDate.now().minusMonths(6));
  }

  private void addClosedMembership(UUID studentId, UUID groupId) {
    jdbcTemplate.update(
        "INSERT INTO group_membership (id, student_id, group_id, start_date, end_date) VALUES"
            + " (?, ?, ?, ?, ?)",
        UUID.randomUUID(),
        studentId,
        groupId,
        LocalDate.now().minusMonths(12),
        LocalDate.now().minusMonths(1));
  }
}
