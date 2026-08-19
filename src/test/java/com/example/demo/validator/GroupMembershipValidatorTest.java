package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.exception.GroupMembershipValidationException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class GroupMembershipValidatorTest {

  private final GroupMembershipValidator validator = new GroupMembershipValidator();

  @Test
  void validateMatriculeFormat_accepts_valid_STDyynnn_matricule() {
    validator.validateMatriculeFormat("STD25001");
    // no exception thrown = pass
  }

  @Test
  void validateMatriculeFormat_rejects_wrong_prefix() {
    assertThatThrownBy(() -> validator.validateMatriculeFormat("TCH25001"))
        .isInstanceOf(GroupMembershipValidationException.class);
  }

  @Test
  void validateMatriculeFormat_rejects_wrong_length() {
    assertThatThrownBy(() -> validator.validateMatriculeFormat("STD2501"))
        .isInstanceOf(GroupMembershipValidationException.class);
  }

  @Test
  void validateMatriculeFormat_rejects_null() {
    assertThatThrownBy(() -> validator.validateMatriculeFormat(null))
        .isInstanceOf(GroupMembershipValidationException.class);
  }

  @Test
  void validateDates_rejects_null_startDate() {
    assertThatThrownBy(() -> validator.validateDates(null, null))
        .isInstanceOf(GroupMembershipValidationException.class);
  }

  @Test
  void validateDates_rejects_endDate_not_after_startDate() {
    var day = LocalDate.of(2025, 1, 1);
    assertThatThrownBy(() -> validator.validateDates(day, day))
        .isInstanceOf(GroupMembershipValidationException.class);
  }

  @Test
  void validateDates_accepts_endDate_strictly_after_startDate() {
    validator.validateDates(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1));
    // no exception thrown = pass
  }

  @Test
  void validateStartDateIsImmutable_rejects_change_once_set() {
    assertThatThrownBy(
            () ->
                validator.validateStartDateIsImmutable(
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 2, 1)))
        .isInstanceOf(GroupMembershipValidationException.class);
  }

  @Test
  void validateStartDateIsImmutable_allows_same_value() {
    var day = LocalDate.of(2025, 1, 1);
    validator.validateStartDateIsImmutable(day, day);
    // no exception thrown = pass
  }
}
