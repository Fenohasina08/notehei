package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GradeValidatorIT extends FacadeIT {

  @Autowired private GradeValidator gradeValidator;

  @Autowired private ExamRepository examRepository;

  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;

  @Autowired private StudentRepository studentRepository;

  private UUID teacherId;
  private UUID courseId;
  private UUID examId;

  @BeforeEach
  void setUp() {
    teacherId = UUID.randomUUID();
    courseId = UUID.randomUUID();
    examId = UUID.randomUUID();
  }

  @Test
  void rejects_when_teacher_is_not_assigned_to_the_exam_course() {

    JExam exam =
            JExam.builder()
                    .id(examId)
                    .courseId(courseId)
                    .build();

    examRepository.save(exam);

    assertThatThrownBy(
            () -> gradeValidator.validateTeacherOwnsExam(teacherId, examId))
            .isInstanceOf(GradeValidationException.class)
            .hasMessageContaining("not assigned");
  }

  @Test
  void rejects_when_both_teacher_and_admin_are_set() {

    var teacherMatricule = "TCH26001";
    var adminId = UUID.randomUUID();

    assertThatThrownBy(
            () -> gradeValidator.validateExactlyOneAuthor(
                    teacherMatricule,
                    adminId))
            .isInstanceOf(GradeValidationException.class);
  }

  @Test
  void rejects_when_neither_teacher_nor_admin_is_set() {

    assertThatThrownBy(
            () -> gradeValidator.validateExactlyOneAuthor(null, null))
            .isInstanceOf(GradeValidationException.class);
  }

  @Test
  void accepts_when_student_requests_their_own_grades() {

    var requesterId = UUID.randomUUID();
    var studentMatricule = "STD26001";

    JStudent student =
            JStudent.builder()
                    .id(requesterId)
                    .matricule(studentMatricule)
                    .build();

    studentRepository.save(student);

    gradeValidator.validateRequesterCanAccessStudentGrades(
            requesterId,
            true,
            studentMatricule);
  }

  @Test
  void rejects_when_student_requests_another_students_grades() {

    var requesterId = UUID.randomUUID();

    JStudent student =
            JStudent.builder()
                    .id(requesterId)
                    .matricule("STD26001")
                    .build();

    studentRepository.save(student);

    assertThatThrownBy(
            () ->
                    gradeValidator.validateRequesterCanAccessStudentGrades(
                            requesterId,
                            true,
                            "STD26999"))
            .isInstanceOf(GradeValidationException.class)
            .hasMessageContaining("cannot access");
  }

  @Test
  void accepts_when_requester_is_not_a_student() {

    var requesterId = UUID.randomUUID();

    gradeValidator.validateRequesterCanAccessStudentGrades(
            requesterId,
            false,
            "STD26001");
  }
}