package com.example.demo.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DiplomaValidationException extends RuntimeException {
  public DiplomaValidationException(String message) {
    super(message);
  }
}