package com.example.demo.exception;

public class GroupMembershipValidationException extends ValidationException {
  public GroupMembershipValidationException(String message) {
    super("GROUP_MEMBERSHIP_VALIDATION_FAILED", message);
  }
}
