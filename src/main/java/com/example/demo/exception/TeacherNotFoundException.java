package com.example.demo.exception;

import java.util.UUID;

public class TeacherNotFoundException extends ResourceNotFoundException {

  public TeacherNotFoundException(UUID id) {
    super("TEACHER_NOT_FOUND", "No teacher found with id " + id);
  }

  public TeacherNotFoundException(String email) {
    super("TEACHER_NOT_FOUND", "No teacher found with email " + email);
  }
}
