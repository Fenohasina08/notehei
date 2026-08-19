package com.example.demo.exception;

public class GroupProgramHistoryValidationException extends ValidationException {
  public GroupProgramHistoryValidationException(String message) {
    super("GROUP_PROGRAM_HISTORY_VALIDATION_FAILED", message);
  }
}
