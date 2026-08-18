package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.GradeValidationException;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeValidatorTest {

  @Mock private ExamRepository examRepository;
  @Mock private TeachingAssignmentRepository teachingAssignmentRepository;
  @Mock private StudentRepository studentRepository;

  private GradeValidator gradeValidator;

  @BeforeEach
  void setUp() {
    gradeValidator =
        new GradeValidator(examRepository, teachingAssignmentRepository, studentRepository);
  }

  @Test
  void accepts_when_teacher_is_assigned_to_the_exam_course() {
    var teacherId = UUID.randomUUID();
    var courseId = UUID.randomUUID();
    var examId = UUID.randomUUID();

    when(examRepository.findById(examId))
        .thenReturn(Optional.of(JExam.builder().id(examId).courseId(courseId).build()));
    when(teachingAssignmentRepository.existsByTeacherIdAndCourseId(teacherId, courseId))
        .thenReturn(true);

    gradeValidator.validateTeacherOwnsExam(teacherId, examId);
  }

  @Test
  void rejects_when_teacher_is_not_assigned_to_the_exam_course() {
    var teacherId = UUID.randomUUID();
    var courseId = UUID.randomUUID();
    var examId = UUID.randomUUID();

    when(examRepository.findById(examId))
        .thenReturn(Optional.of(JExam.builder().id(examId).courseId(courseId).build()));
    when(teachingAssignmentRepository.existsByTeacherIdAndCourseId(teacherId, courseId))
        .thenReturn(false);

    assertThatThrownBy(() -> gradeValidator.validateTeacherOwnsExam(teacherId, examId))
        .isInstanceOf(GradeValidationException.class)
        .hasMessageContaining("not assigned");
  }

  @Test
  void rejects_when_both_teacher_and_admin_are_set() {
    var teacherMatricule = "TEACH001";
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
    var requesterId = UUID.randomUUID();
    var studentMatricule = "STD26001";

    when(studentRepository.findById(requesterId))
        .thenReturn(
            Optional.of(JStudent.builder().id(requesterId).matricule(studentMatricule).build()));

    gradeValidator.validateRequesterCanAccessStudentGrades(requesterId, true, studentMatricule);
  }

  @Test
  void rejects_when_student_requests_another_students_grades() {
    var requesterId = UUID.randomUUID();

    when(studentRepository.findById(requesterId))
        .thenReturn(Optional.of(JStudent.builder().id(requesterId).matricule("STD26001").build()));

    assertThatThrownBy(
            () ->
                gradeValidator.validateRequesterCanAccessStudentGrades(
                    requesterId, true, "STD26999"))
        .isInstanceOf(GradeValidationException.class)
        .hasMessageContaining("cannot access");
  }

  @Test
  void accepts_when_requester_is_not_a_student() {
    var requesterId = UUID.randomUUID();

    gradeValidator.validateRequesterCanAccessStudentGrades(requesterId, false, "STD26001");
  }
}
