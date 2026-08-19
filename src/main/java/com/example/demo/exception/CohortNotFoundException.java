package com.example.demo.exception;

import java.util.UUID;

public class CohortNotFoundException extends ResourceNotFoundException {
  public CohortNotFoundException(UUID id) {
    super("COHORT_NOT_FOUND", "No cohort found with id " + id);
  }
}
