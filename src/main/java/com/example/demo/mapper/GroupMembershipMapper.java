package com.example.demo.mapper;

import com.example.demo.entity.JGroupMembership;
import com.example.demo.model.GroupMembership;
import org.springframework.stereotype.Component;

@Component
public class GroupMembershipMapper {

  public JGroupMembership toEntity(GroupMembership dto) {
    return JGroupMembership.builder()
        .id(dto.id())
        .studentId(dto.studentId())
        .groupId(dto.groupId())
        .startDate(dto.startDate())
        .endDate(dto.endDate())
        .build();
  }

  public GroupMembership toDto(JGroupMembership entity) {
    return new GroupMembership(
        entity.getId(),
        entity.getStudentId(),
        entity.getGroupId(),
        entity.getStartDate(),
        entity.getEndDate());
  }
}
