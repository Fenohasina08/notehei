package com.example.demo.mapper;

import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.entity.JTeacher;

public class TeacherMapper {

  private TeacherMapper() {}

  public static TeacherResponseDTO toResponseDTO(JTeacher teacher) {
    return TeacherResponseDTO.builder()
        .id(teacher.getId())
        .firstName(teacher.getFirstName())
        .lastName(teacher.getLastName())
        .email(teacher.getEmail())
        .birthdate(teacher.getBirthdate())
        .address(teacher.getAddress())
        .matricule(teacher.getMatricule())
        .createdAt(teacher.getCreatedAt().toLocalDateTime())
        .updatedAt(teacher.getUpdatedAt().toLocalDateTime())
        .build();
  }
}
