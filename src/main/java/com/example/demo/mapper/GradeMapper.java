package com.example.demo.mapper;

import com.example.demo.entity.JGrade;
import com.example.demo.model.Grade;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {

  public JGrade toEntity(Grade dto) {
    return JGrade.builder()
        .id(dto.id())
        .studentId(dto.studentId())
        .examId(dto.examId())
        .value(dto.value())
        .status(dto.status())
        .enteredAt(dto.enteredAt())
        .teacherId(dto.teacherId())
        .adminId(dto.adminId())
        .build();
  }

  public Grade toDto(JGrade entity) {
    return new Grade(
        entity.getId(),
        entity.getStudentId(),
        entity.getExamId(),
        entity.getValue(),
        entity.getStatus(),
        entity.getEnteredAt(),
        entity.getTeacherId(),
        entity.getAdminId());
  }
}
