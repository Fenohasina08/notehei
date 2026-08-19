package com.example.demo.exception;

public class ProgramValidationException extends ValidationException {
  public ProgramValidationException(String message) {
    super("PROGRAM_VALIDATION_FAILED", message);
  }
}
