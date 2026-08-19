package com.example.demo.mapper;

import com.example.demo.entity.JAcademicYear;
import com.example.demo.model.AcademicYear;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

  public JAcademicYear toEntity(AcademicYear dto) {
    return JAcademicYear.builder()
        .id(dto.id())
        .name(dto.name())
        .startYear(dto.startYear())
        .endYear(dto.endYear())
        .build();
  }

  public AcademicYear toDto(JAcademicYear entity) {
    return new AcademicYear(
        entity.getId(), entity.getName(), entity.getStartYear(), entity.getEndYear());
  }
}
