package com.example.demo.exception;

public class SemesterValidationException extends ValidationException {
  public SemesterValidationException(String message) {
    super("SEMESTER_VALIDATION_FAILED", message);
  }
}
