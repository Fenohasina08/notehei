package com.example.demo.exception;

public class ExamValidationException extends ValidationException {
  public ExamValidationException(String message) {
    super("EXAM_VALIDATION_FAILED", message);
  }
}
