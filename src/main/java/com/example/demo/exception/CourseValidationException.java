package com.example.demo.exception;

public class CourseValidationException extends ValidationException {

  public CourseValidationException(String message) {
    super("COURSE_VALIDATION_ERROR", message);
  }
}
