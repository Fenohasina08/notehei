package com.example.demo.validator;

import com.example.demo.exception.CourseUnitValidationException;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitProgramRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Enforces that a course unit is only considered "complete"/publishable once it has at least one
 * program (via {@code course_unit_program}) and at least one course (via {@code
 * course_unit_course}) attached. Structural attachment itself (linking a program or a course) is
 * plain CRUD, allowed to happen incrementally — this validator is the gate a caller (e.g. an admin
 * "publish course unit" action, or {@code SemesterCreditValidator}'s completeness check) uses
 * before treating the course unit as ready.
 */
@Component
@AllArgsConstructor
public class CourseUnitValidator {

  private final CourseUnitProgramRepository courseUnitProgramRepository;
  private final CourseUnitCourseRepository courseUnitCourseRepository;

  public void validateIsComplete(UUID courseUnitId) {
    boolean hasProgram = courseUnitProgramRepository.existsByCourseUnitId(courseUnitId);
    if (!hasProgram) {
      throw new CourseUnitValidationException(
          "Course unit " + courseUnitId + " must be attached to at least one program");
    }

    List<?> courses = courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId));
    if (courses.isEmpty()) {
      throw new CourseUnitValidationException(
          "Course unit " + courseUnitId + " must have at least one course attached");
    }
  }
}
