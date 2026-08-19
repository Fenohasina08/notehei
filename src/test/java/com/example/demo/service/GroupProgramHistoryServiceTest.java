package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JGroupProgramHistory;
import com.example.demo.mapper.GroupProgramHistoryMapper;
import com.example.demo.repository.GroupProgramHistoryRepository;
import com.example.demo.validator.GroupProgramHistoryValidator;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GroupProgramHistoryServiceTest {

  @Mock private GroupProgramHistoryRepository groupProgramHistoryRepository;

  private GroupProgramHistoryService groupProgramHistoryService;

  private UUID groupId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    groupProgramHistoryService =
        new GroupProgramHistoryService(
            groupProgramHistoryRepository,
            new GroupProgramHistoryMapper(),
            new GroupProgramHistoryValidator());
    groupId = UUID.randomUUID();
    when(groupProgramHistoryRepository.save(any(JGroupProgramHistory.class)))
        .thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void assignProgram_closes_the_currently_active_program_before_opening_the_new_one() {
    var startDate = LocalDate.of(2025, 9, 1);
    var activeEntry =
        JGroupProgramHistory.builder()
            .id(UUID.randomUUID())
            .groupId(groupId)
            .programId(UUID.randomUUID())
            .startDate(LocalDate.of(2023, 9, 1))
            .endDate(null)
            .build();

    when(groupProgramHistoryRepository.findByGroupIdAndEndDateIsNull(groupId))
        .thenReturn(Optional.of(activeEntry));

    groupProgramHistoryService.assignProgram(groupId, UUID.randomUUID(), startDate);

    assertThat(activeEntry.getEndDate()).isEqualTo(startDate);
    verify(groupProgramHistoryRepository, times(2)).save(any(JGroupProgramHistory.class));
  }

  @Test
  void assignProgram_only_creates_a_new_entry_when_none_was_active() {
    when(groupProgramHistoryRepository.findByGroupIdAndEndDateIsNull(groupId))
        .thenReturn(Optional.empty());

    groupProgramHistoryService.assignProgram(groupId, UUID.randomUUID(), LocalDate.of(2025, 9, 1));

    ArgumentCaptor<JGroupProgramHistory> captor =
        ArgumentCaptor.forClass(JGroupProgramHistory.class);
    verify(groupProgramHistoryRepository, times(1)).save(captor.capture());
    assertThat(captor.getValue().getEndDate()).isNull();
  }
}
