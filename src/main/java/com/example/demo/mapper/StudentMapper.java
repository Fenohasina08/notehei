package com.example.demo.mapper;

import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.entity.JStudent;

public class StudentMapper {

  private StudentMapper() {}

  public static StudentResponseDTO toResponseDTO(JStudent student) {
    return StudentResponseDTO.builder()
        .id(student.getId())
        .firstName(student.getFirstName())
        .lastName(student.getLastName())
        .email(student.getEmail())
        .birthdate(student.getBirthdate())
        .address(student.getAddress())
        .matricule(student.getMatricule())
        .createdAt(student.getCreatedAt().toLocalDateTime())
        .updatedAt(student.getUpdatedAt().toLocalDateTime())
        .build();
  }
}
