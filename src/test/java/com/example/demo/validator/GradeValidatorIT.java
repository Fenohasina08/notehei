package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.GradeValidationException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GradeValidatorIT extends FacadeIT {

  @Autowired private GradeValidator gradeValidator;
  @Autowired private ExamRepository examRepository;
  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private CourseRepository courseRepository;

  private UUID teacherId;
  private UUID courseId;

  @BeforeEach
  void setUp() {
    teacherId = UUID.randomUUID();

    JCourse course =
        JCourse.builder()
            .reference("GRADE-VAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8))
            .title("Grade Validator Test Course")
            .coefficient(new BigDecimal("1.00"))
            .build();
    course = courseRepository.saveAndFlush(course);
    courseId = course.getId();
  }

  @Test
  void rejects_when_teacher_is_not_assigned_to_the_exam_course() {
    // Sauvegarde sans id explicite
    JExam exam =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(new BigDecimal("100.00"))
            .build();
    exam = examRepository.saveAndFlush(exam);
    UUID examId = exam.getId(); // ID généré

    assertThatThrownBy(() -> gradeValidator.validateTeacherOwnsExam(teacherId, examId))
        .isInstanceOf(GradeValidationException.class)
        .hasMessageContaining("not assigned");
  }

  @Test
  void rejects_when_both_teacher_and_admin_are_set() {
    var teacherMatricule = "TCH26001";
    var adminId = UUID.randomUUID();

    assertThatThrownBy(() -> gradeValidator.validateExactlyOneAuthor(teacherMatricule, adminId))
        .isInstanceOf(GradeValidationException.class);
  }

  @Test
  void rejects_when_neither_teacher_nor_admin_is_set() {
    assertThatThrownBy(() -> gradeValidator.validateExactlyOneAuthor(null, null))
        .isInstanceOf(GradeValidationException.class);
  }

  @Test
  void accepts_when_student_requests_their_own_grades() {
    JStudent student =
        JStudent.builder()
            .firstName("Test")
            .lastName("Student")
            .email("student-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .address("Antananarivo")
            .matricule("STD26001")
            .build();
    student = studentRepository.saveAndFlush(student);
    UUID studentId = student.getId();

    gradeValidator.validateRequesterCanAccessStudentGrades(studentId, true, "STD26001");
  }

  @Test
  void rejects_when_student_requests_another_students_grades() {
    JStudent student =
        JStudent.builder()
            .firstName("Test")
            .lastName("Student")
            .email("student-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .address("Antananarivo")
            .matricule("STD26001")
            .build();
    student = studentRepository.saveAndFlush(student);
    UUID studentId = student.getId();

    assertThatThrownBy(
            () ->
                gradeValidator.validateRequesterCanAccessStudentGrades(studentId, true, "STD26999"))
        .isInstanceOf(GradeValidationException.class)
        .hasMessageContaining("cannot access");
  }

  @Test
  void accepts_when_requester_is_not_a_student() {
    var requesterId = UUID.randomUUID();

    gradeValidator.validateRequesterCanAccessStudentGrades(requesterId, false, "STD26001");
  }
}
