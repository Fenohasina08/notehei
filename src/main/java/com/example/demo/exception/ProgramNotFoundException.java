package com.example.demo.exception;

import java.util.UUID;

public class ProgramNotFoundException extends ResourceNotFoundException {
  public ProgramNotFoundException(UUID id) {
    super("PROGRAM_NOT_FOUND", "No program found with id " + id);
  }
}
