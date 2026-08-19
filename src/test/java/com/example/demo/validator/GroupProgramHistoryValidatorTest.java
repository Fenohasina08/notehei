package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.exception.GroupProgramHistoryValidationException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class GroupProgramHistoryValidatorTest {

  private final GroupProgramHistoryValidator validator = new GroupProgramHistoryValidator();

  @Test
  void validateDates_rejects_null_startDate() {
    assertThatThrownBy(() -> validator.validateDates(null, null))
        .isInstanceOf(GroupProgramHistoryValidationException.class);
  }

  @Test
  void validateDates_rejects_endDate_not_after_startDate() {
    var day = LocalDate.of(2025, 1, 1);
    assertThatThrownBy(() -> validator.validateDates(day, day))
        .isInstanceOf(GroupProgramHistoryValidationException.class);
  }

  @Test
  void validateDates_accepts_valid_range() {
    validator.validateDates(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1));
    // no exception thrown = pass
  }

  @Test
  void validateNoOtherActivePeriod_rejects_when_one_already_exists() {
    assertThatThrownBy(() -> validator.validateNoOtherActivePeriod(true))
        .isInstanceOf(GroupProgramHistoryValidationException.class);
  }

  @Test
  void validateNoOtherActivePeriod_allows_when_none_exists() {
    validator.validateNoOtherActivePeriod(false);
    // no exception thrown = pass
  }
}
