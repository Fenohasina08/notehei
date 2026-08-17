package com.example.demo.exception;

public class GradeValidationException extends ValidationException {
  public GradeValidationException(String message) {
    super("GRADE_VALIDATION_FAILED", message);
  }
}
