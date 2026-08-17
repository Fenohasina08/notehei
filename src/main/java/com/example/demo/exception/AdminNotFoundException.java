package com.example.demo.exception;

import java.util.UUID;

public class AdminNotFoundException extends ResourceNotFoundException {

  public AdminNotFoundException(UUID id) {
    super("ADMIN_NOT_FOUND", "No admin found with id " + id);
  }

  public AdminNotFoundException(String email) {
    super("ADMIN_NOT_FOUND", "No admin found with email " + email);
  }
}
