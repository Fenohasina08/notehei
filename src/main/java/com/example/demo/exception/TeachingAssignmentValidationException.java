package com.example.demo.exception;

public class TeachingAssignmentValidationException extends ValidationException {
  public TeachingAssignmentValidationException(String message) {
    super("TEACHING_ASSIGNMENT_VALIDATION_FAILED", message);
  }
}
