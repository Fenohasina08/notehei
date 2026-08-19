package com.example.demo.exception;

public class AcademicYearValidationException extends ValidationException {
  public AcademicYearValidationException(String message) {
    super("ACADEMIC_YEAR_VALIDATION_FAILED", message);
  }
}
