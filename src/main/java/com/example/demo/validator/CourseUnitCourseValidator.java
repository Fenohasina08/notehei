package com.example.demo.validator;

import com.example.demo.exception.CourseUnitNotFoundException;
import com.example.demo.exception.CourseValidationException;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Enforces: the sum of {@code course_unit_course.credits} for a given course unit must equal
 * exactly {@code course_unit.credits}. Complements {@link SemesterCreditValidator}, which checks
 * the level above (sum of course_unit credits per semester = 30).
 */
@Component
@AllArgsConstructor
public class CourseUnitCourseValidator {

  private final CourseUnitRepository courseUnitRepository;
  private final CourseUnitCourseRepository courseUnitCourseRepository;

  public void validateCreditsMatchCourseUnit(UUID courseUnitId) {
    var courseUnit =
        courseUnitRepository
            .findById(courseUnitId)
            .orElseThrow(() -> new CourseUnitNotFoundException(courseUnitId));

    int sumOfCourseCredits = sumOfCourseCredits(courseUnitId);

    if (sumOfCourseCredits != courseUnit.getCredits()) {
      throw new CourseValidationException(
          "Sum of course_unit_course credits ("
              + sumOfCourseCredits
              + ") must equal course_unit.credits ("
              + courseUnit.getCredits()
              + ") for course unit "
              + courseUnitId);
    }
  }

  /** Rejects attaching a course whose credits would push the running total above UE.credits. */
  public void validateDoesNotExceedCourseUnitCredits(UUID courseUnitId, int candidateCredits) {
    var courseUnit =
        courseUnitRepository
            .findById(courseUnitId)
            .orElseThrow(() -> new CourseUnitNotFoundException(courseUnitId));

    if (candidateCredits > courseUnit.getCredits()) {
      throw new CourseValidationException(
          "Sum of course_unit_course credits ("
              + candidateCredits
              + ") would exceed course_unit.credits ("
              + courseUnit.getCredits()
              + ") for course unit "
              + courseUnitId);
    }
  }

  private int sumOfCourseCredits(UUID courseUnitId) {
    return courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)).stream()
        .mapToInt(link -> link.getCredits() == null ? 0 : link.getCredits())
        .sum();
  }
}
