package com.example.demo.mapper;

import com.example.demo.entity.JSemester;
import com.example.demo.model.Semester;
import org.springframework.stereotype.Component;

@Component
public class SemesterMapper {

  public JSemester toEntity(Semester dto) {
    return JSemester.builder()
        .id(dto.id())
        .number(dto.number())
        .cohortId(dto.cohortId())
        .academicYearId(dto.academicYearId())
        .build();
  }

  public Semester toDto(JSemester entity) {
    return new Semester(
        entity.getId(), entity.getNumber(), entity.getCohortId(), entity.getAcademicYearId());
  }
}
