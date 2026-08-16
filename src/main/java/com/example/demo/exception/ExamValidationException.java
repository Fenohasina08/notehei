package com.example.demo.exception;

public class ExamValidationException extends RuntimeException {
  public ExamValidationException(String message) {
    super(message);
  }
}
