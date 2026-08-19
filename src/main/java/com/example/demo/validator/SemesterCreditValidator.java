package com.example.demo.validator;

import com.example.demo.exception.SemesterValidationException;
import com.example.demo.repository.CourseUnitRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Enforces the academic rule: the sum of {@code course_unit.credits} attached to a semester must
 * equal exactly 30 (ECTS-style). Called by {@code CourseUnitService} whenever a course unit is
 * attached to/detached from a semester, and exposed for callers (e.g. an admin "publish semester
 * structure" action) that want to check completeness without mutating anything.
 */
@Component
@AllArgsConstructor
public class SemesterCreditValidator {

  private static final int REQUIRED_TOTAL_CREDITS = 30;

  private final CourseUnitRepository courseUnitRepository;

  public int totalCredits(UUID semesterId) {
    return courseUnitRepository.findBySemesterId(semesterId).stream()
        .mapToInt(cu -> cu.getCredits() == null ? 0 : cu.getCredits())
        .sum();
  }

  public boolean isComplete(UUID semesterId) {
    return totalCredits(semesterId) == REQUIRED_TOTAL_CREDITS;
  }

  /**
   * Rejects a course unit's credits if adding/keeping it would push the semester's total strictly
   * above 30. (Being below 30 is allowed while the structure is still being built — only "> 30" is
   * an immediate, unrecoverable error; "== 30" is checked separately via {@link #isComplete}.)
   */
  public void validateDoesNotExceedThirty(UUID semesterId, int candidateTotalCredits) {
    if (candidateTotalCredits > REQUIRED_TOTAL_CREDITS) {
      throw new SemesterValidationException(
          "The sum of course_unit credits for semester "
              + semesterId
              + " would be "
              + candidateTotalCredits
              + ", which exceeds the required total of "
              + REQUIRED_TOTAL_CREDITS);
    }
  }
}
