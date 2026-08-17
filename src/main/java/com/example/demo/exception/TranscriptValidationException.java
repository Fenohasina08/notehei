package com.example.demo.exception;

public class TranscriptValidationException extends ValidationException {
  public TranscriptValidationException(String message) {
    super("TRANSCRIPT_VALIDATION_FAILED", message);
  }
}
