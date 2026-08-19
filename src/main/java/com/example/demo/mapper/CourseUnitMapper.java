package com.example.demo.mapper;

import com.example.demo.entity.JCourseUnit;
import com.example.demo.model.CourseUnit;
import org.springframework.stereotype.Component;

@Component
public class CourseUnitMapper {

  public JCourseUnit toEntity(CourseUnit dto) {
    return JCourseUnit.builder()
        .id(dto.id())
        .code(dto.code())
        .name(dto.name())
        .credits(dto.credits())
        .semesterId(dto.semesterId())
        .build();
  }

  public CourseUnit toDto(JCourseUnit entity) {
    return new CourseUnit(
        entity.getId(),
        entity.getCode(),
        entity.getName(),
        entity.getCredits(),
        entity.getSemesterId());
  }
}
