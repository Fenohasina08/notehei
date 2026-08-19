package com.example.demo.service;

import com.example.demo.mapper.GroupProgramHistoryMapper;
import com.example.demo.model.GroupProgramHistory;
import com.example.demo.repository.GroupProgramHistoryRepository;
import com.example.demo.validator.GroupProgramHistoryValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GroupProgramHistoryService {

  private final GroupProgramHistoryRepository groupProgramHistoryRepository;
  private final GroupProgramHistoryMapper groupProgramHistoryMapper;
  private final GroupProgramHistoryValidator groupProgramHistoryValidator;

  /**
   * Assigns a new program to a group starting on {@code startDate}. If the group already has an
   * active program, it is closed (its {@code endDate} set to {@code startDate}) so the "one active
   * program per group" invariant always holds.
   */
  @Transactional
  public GroupProgramHistory assignProgram(UUID groupId, UUID programId, LocalDate startDate) {
    groupProgramHistoryValidator.validateDates(startDate, null);

    groupProgramHistoryRepository
        .findByGroupIdAndEndDateIsNull(groupId)
        .ifPresent(
            active -> {
              active.setEndDate(startDate);
              groupProgramHistoryRepository.save(active);
            });

    var entity =
        groupProgramHistoryMapper.toEntity(
            new GroupProgramHistory(null, groupId, programId, startDate, null));
    return groupProgramHistoryMapper.toDto(groupProgramHistoryRepository.save(entity));
  }

  public List<GroupProgramHistory> getHistoryForGroup(UUID groupId) {
    return groupProgramHistoryRepository.findByGroupIdOrderByStartDateDesc(groupId).stream()
        .map(groupProgramHistoryMapper::toDto)
        .toList();
  }

  public GroupProgramHistory getActiveProgram(UUID groupId) {
    return groupProgramHistoryRepository
        .findByGroupIdAndEndDateIsNull(groupId)
        .map(groupProgramHistoryMapper::toDto)
        .orElse(null);
  }
}
