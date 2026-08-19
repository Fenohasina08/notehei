package com.example.demo.exception;

import java.util.UUID;

public class GroupNotFoundException extends ResourceNotFoundException {
  public GroupNotFoundException(UUID id) {
    super("GROUP_NOT_FOUND", "No group found with id " + id);
  }
}
