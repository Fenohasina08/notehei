package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JGroup;
import com.example.demo.entity.JGroupProgramHistory;
import com.example.demo.entity.JProgram;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupProgramHistoryRepositoryIT extends FacadeIT {

  @Autowired private GroupProgramHistoryRepository groupProgramHistoryRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private ProgramRepository programRepository;

  private JGroup group;
  private JProgram program;

  @BeforeEach
  void setUp() {
    groupProgramHistoryRepository.deleteAll();
    groupRepository.deleteAll();
    cohortRepository.deleteAll();
    programRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2035).build());
    group =
        groupRepository.save(JGroup.builder().reference("GPH1").cohortId(cohort.getId()).build());
    program = programRepository.save(JProgram.builder().code("GPH").name("Test Program").build());
  }

  @Test
  void findByGroupIdAndEndDateIsNull_returns_the_open_entry_only() {
    groupProgramHistoryRepository.save(
        JGroupProgramHistory.builder()
            .groupId(group.getId())
            .programId(program.getId())
            .startDate(LocalDate.of(2020, 9, 1))
            .endDate(LocalDate.of(2022, 9, 1))
            .build());

    var active =
        groupProgramHistoryRepository.save(
            JGroupProgramHistory.builder()
                .groupId(group.getId())
                .programId(program.getId())
                .startDate(LocalDate.of(2022, 9, 1))
                .endDate(null)
                .build());

    var found = groupProgramHistoryRepository.findByGroupIdAndEndDateIsNull(group.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(active.getId());
  }

  @Test
  void findByGroupIdOrderByStartDateDesc_orders_most_recent_first() {
    groupProgramHistoryRepository.save(
        JGroupProgramHistory.builder()
            .groupId(group.getId())
            .programId(program.getId())
            .startDate(LocalDate.of(2020, 9, 1))
            .endDate(LocalDate.of(2022, 9, 1))
            .build());
    groupProgramHistoryRepository.save(
        JGroupProgramHistory.builder()
            .groupId(group.getId())
            .programId(program.getId())
            .startDate(LocalDate.of(2022, 9, 1))
            .endDate(null)
            .build());

    var history = groupProgramHistoryRepository.findByGroupIdOrderByStartDateDesc(group.getId());

    assertThat(history).hasSize(2);
    assertThat(history.get(0).getStartDate()).isEqualTo(LocalDate.of(2022, 9, 1));
  }
}
