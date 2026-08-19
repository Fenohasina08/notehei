package com.example.demo.exception;

import java.util.UUID;

public class CourseUnitNotFoundException extends ResourceNotFoundException {
  public CourseUnitNotFoundException(UUID id) {
    super("COURSE_UNIT_NOT_FOUND", "No course unit found with id " + id);
  }
}
