package com.example.demo.exception;

import java.util.UUID;

public class StudentNotFoundException extends ResourceNotFoundException {

  public StudentNotFoundException(UUID id) {
    super("STUDENT_NOT_FOUND", "No student found with id " + id);
  }

  public StudentNotFoundException(String email) {
    super("STUDENT_NOT_FOUND", "No student found with email " + email);
  }
}
