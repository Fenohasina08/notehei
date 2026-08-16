package com.example.demo.mapper;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.model.Exam;
import com.example.demo.model.ExamType;
import org.springframework.stereotype.Component;

@Component
public class ExamMapper {

  public JExam toEntity(Exam dto) {
    return JExam.builder()
        .id(dto.id())
        .courseId(dto.courseId())
        .type(JExamType.valueOf(dto.type().name()))
        .examDate(dto.examDate())
        .weighting(dto.weighting())
        .build();
  }

  public Exam toDto(JExam entity) {
    return new Exam(
        entity.getId(),
        entity.getCourseId(),
        ExamType.valueOf(entity.getType().name()),
        entity.getExamDate(),
        entity.getWeighting());
  }
}
