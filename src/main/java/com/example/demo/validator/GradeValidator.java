package com.example.demo.validator;

import com.example.demo.exception.GradeValidationException;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GradeValidator {

  private final ExamRepository examRepository;
  private final TeachingAssignmentRepository teachingAssignmentRepository;

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

  public void validateExactlyOneAuthor(UUID teacherId, UUID adminId) {
    var teacherSet = teacherId != null;
    var adminSet = adminId != null;
    if (teacherSet == adminSet) {
      throw new GradeValidationException(
          "A grade must have exactly one author: either a teacher or an admin");
    }
  }
}
