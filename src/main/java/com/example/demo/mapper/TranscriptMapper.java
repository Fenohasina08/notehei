package com.example.demo.mapper;

import com.example.demo.entity.JTranscript;
import com.example.demo.model.Transcript;
import org.springframework.stereotype.Component;

@Component
public class TranscriptMapper {

  public JTranscript toEntity(Transcript dto) {
    return JTranscript.builder()
        .id(dto.id())
        .studentId(dto.studentId())
        .semesterId(dto.semesterId())
        .s3Url(dto.s3Url())
        .status(dto.status())
        .generatedAt(dto.generatedAt())
        .build();
  }

  public Transcript toDto(JTranscript entity) {
    return new Transcript(
        entity.getId(),
        entity.getStudentId(),
        entity.getSemesterId(),
        entity.getS3Url(),
        entity.getStatus(),
        entity.getGeneratedAt());
  }
}
