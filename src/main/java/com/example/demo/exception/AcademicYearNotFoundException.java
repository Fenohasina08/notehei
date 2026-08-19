package com.example.demo.exception;

import java.util.UUID;

public class AcademicYearNotFoundException extends ResourceNotFoundException {
  public AcademicYearNotFoundException(UUID id) {
    super("ACADEMIC_YEAR_NOT_FOUND", "No academic year found with id " + id);
  }
}
