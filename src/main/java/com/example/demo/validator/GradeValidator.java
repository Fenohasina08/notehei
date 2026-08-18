package com.example.demo.validator;

import com.example.demo.exception.GradeValidationException;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GradeValidator {

  private final ExamRepository examRepository;
  private final TeachingAssignmentRepository teachingAssignmentRepository;
  private final StudentRepository studentRepository;

  public void validateTeacherOwnsExam(UUID teacherId, UUID examId) {
    var exam =
        examRepository
            .findById(examId)
            .orElseThrow(() -> new GradeValidationException("Exam not found: " + examId));

    var isAssigned =
        teachingAssignmentRepository.existsByTeacherIdAndCourseId(teacherId, exam.getCourseId());

    if (!isAssigned) {
      throw new GradeValidationException(
          "Teacher " + teacherId + " is not assigned to course " + exam.getCourseId());
    }
  }

  public void validateExactlyOneAuthor(String teacherMatricule, UUID adminId) {
    var teacherSet = teacherMatricule != null;
    var adminSet = adminId != null;

    if (teacherSet == adminSet) {
      throw new GradeValidationException(
          "A grade must have exactly one author: either a teacher or an admin");
    }
  }

  public void validateRequesterCanAccessStudentGrades(
      UUID requesterId, boolean requesterIsStudent, String studentMatricule) {
    if (!requesterIsStudent) {
      return;
    }

    var student =
        studentRepository
            .findById(requesterId)
            .orElseThrow(() -> new GradeValidationException("Student not found: " + requesterId));

    if (!student.getMatricule().equals(studentMatricule)) {
      throw new GradeValidationException(
          "Requester " + requesterId + " cannot access the grades of " + studentMatricule);
    }
  }
}
