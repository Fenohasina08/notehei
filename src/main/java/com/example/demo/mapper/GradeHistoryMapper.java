package com.example.demo.mapper;

import com.example.demo.entity.JGradeHistory;
import com.example.demo.model.GradeHistory;
import org.springframework.stereotype.Component;

@Component
public class GradeHistoryMapper {

  public JGradeHistory toEntity(GradeHistory dto) {
    return JGradeHistory.builder()
        .id(dto.id())
        .gradeId(dto.gradeId())
        .oldValue(dto.oldValue())
        .newValue(dto.newValue())
        .reason(dto.reason())
        .modifiedAt(dto.modifiedAt())
        .teacherMatricule(dto.teacherMatricule())
        .adminId(dto.adminId())
        .build();
  }

  public GradeHistory toDto(JGradeHistory entity) {
    return new GradeHistory(
        entity.getId(),
        entity.getGradeId(),
        entity.getOldValue(),
        entity.getNewValue(),
        entity.getReason(),
        entity.getModifiedAt(),
        entity.getTeacherMatricule(),
        entity.getAdminId());
  }
}
