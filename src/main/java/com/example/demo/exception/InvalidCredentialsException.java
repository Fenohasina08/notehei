package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {

  public InvalidCredentialsException() {
    super("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid email or password");
  }
}
