package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends BusinessException {

  protected ConflictException(String code, String message) {
    super(code, HttpStatus.CONFLICT, message);
  }
}
