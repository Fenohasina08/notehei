package com.example.demo.exception;

import java.util.UUID;

public class SemesterNotFoundException extends ResourceNotFoundException {
  public SemesterNotFoundException(UUID id) {
    super("SEMESTER_NOT_FOUND", "No semester found with id " + id);
  }
}
