package com.example.demo.exception;

public class CohortValidationException extends ValidationException {
  public CohortValidationException(String message) {
    super("COHORT_VALIDATION_FAILED", message);
  }
}
