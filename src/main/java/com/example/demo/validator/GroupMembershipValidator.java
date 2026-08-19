package com.example.demo.validator;

import com.example.demo.exception.GroupMembershipValidationException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Business rules for group membership, kept separate from Security (this never checks who is
 * calling, only whether the data itself is valid):
 *
 * <ul>
 *   <li>the owning student's matricule must follow the {@code STDyynnn} shape (2-digit entry year +
 *       3-digit sequence — matches {@code MatriculeGeneratorImpl})
 *   <li>a membership's {@code startDate} is immutable once created — only {@code endDate} can be
 *       set later, to close it
 *   <li>a student has at most one *active* (open-ended) membership at a time — the "repeating a
 *       year" case is modeled by closing the current membership and opening a new one in a
 *       (possibly different) group, exactly like a normal group change
 * </ul>
 */
@Component
public class GroupMembershipValidator {

  private static final Pattern MATRICULE_PATTERN = Pattern.compile("^STD\\d{2}\\d{3}$");

  public void validateMatriculeFormat(String matricule) {
    if (matricule == null || !MATRICULE_PATTERN.matcher(matricule).matches()) {
      throw new GroupMembershipValidationException(
          "Student matricule must follow the STDyynnn format, got: " + matricule);
    }
  }

  public void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate == null) {
      throw new GroupMembershipValidationException("startDate is required");
    }
    if (endDate != null && !endDate.isAfter(startDate)) {
      throw new GroupMembershipValidationException("endDate must be strictly after startDate");
    }
  }

  public void validateStartDateIsImmutable(LocalDate existingStartDate, LocalDate newStartDate) {
    if (existingStartDate != null && !existingStartDate.equals(newStartDate)) {
      throw new GroupMembershipValidationException(
          "startDate cannot be modified once a membership has been created");
    }
  }
}
