package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public abstract class ResourceNotFoundException extends BusinessException {

  protected ResourceNotFoundException(String code, String message) {
    super(code, HttpStatus.NOT_FOUND, message);
  }
}
