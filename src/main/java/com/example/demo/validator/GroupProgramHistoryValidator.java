package com.example.demo.validator;

import com.example.demo.exception.GroupProgramHistoryValidationException;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Enforces "a group has at most one *active* program at any given time" (mirrors the DB partial
 * unique index {@code ux_group_program_active}). {@code GroupProgramHistoryService} is expected to
 * close the previous active row (set its {@code endDate}) before/while opening a new one — this
 * validator only checks the shape of a single row/transition, never touches the repository.
 */
@Component
@AllArgsConstructor
public class GroupProgramHistoryValidator {

  public void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate == null) {
      throw new GroupProgramHistoryValidationException("startDate is required");
    }
    if (endDate != null && !endDate.isAfter(startDate)) {
      throw new GroupProgramHistoryValidationException("endDate must be strictly after startDate");
    }
  }

  /**
   * Called before opening a new active period: fails loudly if the caller forgot to close the
   * previous one, instead of relying on the DB constraint to surface a generic conflict.
   */
  public void validateNoOtherActivePeriod(boolean anotherActivePeriodExists) {
    if (anotherActivePeriodExists) {
      throw new GroupProgramHistoryValidationException(
          "This group already has an active program; close it (set its endDate) before starting"
              + " a new one");
    }
  }
}
