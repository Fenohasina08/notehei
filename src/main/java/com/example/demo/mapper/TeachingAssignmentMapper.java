package com.example.demo.mapper;

import com.example.demo.entity.JTeachingAssignment;
import com.example.demo.model.TeachingAssignment;
import org.springframework.stereotype.Component;

@Component
public class TeachingAssignmentMapper {

  public JTeachingAssignment toEntity(TeachingAssignment dto) {
    return JTeachingAssignment.builder()
        .id(dto.id())
        .teacherId(dto.teacherId())
        .courseId(dto.courseId())
        .groupId(dto.groupId())
        .build();
  }

  public TeachingAssignment toDto(JTeachingAssignment entity) {
    return new TeachingAssignment(
        entity.getId(), entity.getTeacherId(), entity.getCourseId(), entity.getGroupId());
  }
}
