package com.example.demo.mapper;

import com.example.demo.entity.JGroupProgramHistory;
import com.example.demo.model.GroupProgramHistory;
import org.springframework.stereotype.Component;

@Component
public class GroupProgramHistoryMapper {

  public JGroupProgramHistory toEntity(GroupProgramHistory dto) {
    return JGroupProgramHistory.builder()
        .id(dto.id())
        .groupId(dto.groupId())
        .programId(dto.programId())
        .startDate(dto.startDate())
        .endDate(dto.endDate())
        .build();
  }

  public GroupProgramHistory toDto(JGroupProgramHistory entity) {
    return new GroupProgramHistory(
        entity.getId(),
        entity.getGroupId(),
        entity.getProgramId(),
        entity.getStartDate(),
        entity.getEndDate());
  }
}
