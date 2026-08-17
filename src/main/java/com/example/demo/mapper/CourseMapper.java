package com.example.demo.mapper;

import com.example.demo.entity.JCourse;
import com.example.demo.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

  public JCourse toEntity(Course dto) {
    return JCourse.builder()
        .id(dto.id())
        .reference(dto.reference())
        .title(dto.title())
        .coefficient(dto.coefficient())
        .build();
  }

  public Course toDto(JCourse entity) {
    return new Course(
        entity.getId(), entity.getReference(), entity.getTitle(), entity.getCoefficient());
  }
}
