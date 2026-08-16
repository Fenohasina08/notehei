package com.example.demo.mapper;

import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.entity.JAdmin;

public class AdminMapper {

  private AdminMapper() {}

  public static AdminResponseDTO toResponseDTO(JAdmin admin) {
    return AdminResponseDTO.builder()
        .id(admin.getId())
        .firstName(admin.getFirstName())
        .lastName(admin.getLastName())
        .email(admin.getEmail())
        .birthdate(admin.getBirthdate())
        .address(admin.getAddress())
        .createdAt(admin.getCreatedAt().toLocalDateTime())
        .updatedAt(admin.getUpdatedAt().toLocalDateTime())
        .build();
  }
}
