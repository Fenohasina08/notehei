package com.example.demo.exception;

import java.util.UUID;

public class TeachingAssignmentNotFoundException extends ResourceNotFoundException {
  public TeachingAssignmentNotFoundException(UUID id) {
    super("TEACHING_ASSIGNMENT_NOT_FOUND", "No teaching assignment found with id " + id);
  }
}
