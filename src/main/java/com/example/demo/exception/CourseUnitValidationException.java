package com.example.demo.exception;

public class CourseUnitValidationException extends ValidationException {
  public CourseUnitValidationException(String message) {
    super("COURSE_UNIT_VALIDATION_FAILED", message);
  }
}
