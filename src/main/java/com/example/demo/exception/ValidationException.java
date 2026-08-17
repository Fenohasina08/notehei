package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public abstract class ValidationException extends BusinessException {

  protected ValidationException(String code, String message) {
    super(code, HttpStatus.UNPROCESSABLE_ENTITY, message);
  }
}
