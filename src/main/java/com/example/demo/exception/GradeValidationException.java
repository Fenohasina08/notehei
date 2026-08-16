package com.example.demo.exception;

public class GradeValidationException extends RuntimeException {
  public GradeValidationException(String message) {
    super(message);
  }
}
