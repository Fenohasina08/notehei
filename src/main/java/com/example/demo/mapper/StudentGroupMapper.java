package com.example.demo.mapper;

import com.example.demo.entity.JGroup;
import com.example.demo.model.StudentGroup;
import org.springframework.stereotype.Component;

@Component
public class StudentGroupMapper {

  public JGroup toEntity(StudentGroup dto) {
    return JGroup.builder()
        .id(dto.id())
        .reference(dto.reference())
        .cohortId(dto.cohortId())
        .build();
  }

  public StudentGroup toDto(JGroup entity) {
    return new StudentGroup(entity.getId(), entity.getReference(), entity.getCohortId());
  }
}
