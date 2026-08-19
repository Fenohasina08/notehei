package com.example.demo.exception;

public class GroupValidationException extends ValidationException {
  public GroupValidationException(String message) {
    super("GROUP_VALIDATION_FAILED", message);
  }
}
