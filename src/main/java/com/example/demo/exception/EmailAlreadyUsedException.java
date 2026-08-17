package com.example.demo.exception;

public class EmailAlreadyUsedException extends ConflictException {

  public EmailAlreadyUsedException(String email) {
    super("EMAIL_ALREADY_USED", "Email already in use: " + email);
  }
}
