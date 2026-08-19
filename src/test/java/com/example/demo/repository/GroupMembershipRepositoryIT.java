package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JGroup;
import com.example.demo.entity.JGroupMembership;
import com.example.demo.entity.JStudent;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupMembershipRepositoryIT extends FacadeIT {

  @Autowired private GroupMembershipRepository groupMembershipRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private StudentRepository studentRepository;

  private JStudent student;
  private JGroup group;

  @BeforeEach
  void setUp() {
    groupMembershipRepository.deleteAll();
    studentRepository.deleteAll();
    groupRepository.deleteAll();
    cohortRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2034).build());
    group =
        groupRepository.save(JGroup.builder().reference("GM1").cohortId(cohort.getId()).build());
    student =
        studentRepository.save(
            JStudent.builder()
                .firstName("Ny")
                .lastName("Aina")
                .email("membership-it@notehei.local")
                .password("secret")
                .address("Antananarivo")
                .matricule("STD25099")
                .build());
  }

  @Test
  void findByStudentIdAndEndDateIsNull_returns_only_the_open_membership() {
    groupMembershipRepository.save(
        JGroupMembership.builder()
            .studentId(student.getId())
            .groupId(group.getId())
            .startDate(LocalDate.of(2020, 9, 1))
            .endDate(LocalDate.of(2021, 6, 1))
            .build());

    var active =
        groupMembershipRepository.save(
            JGroupMembership.builder()
                .studentId(student.getId())
                .groupId(group.getId())
                .startDate(LocalDate.of(2021, 9, 1))
                .endDate(null)
                .build());

    var found = groupMembershipRepository.findByStudentIdAndEndDateIsNull(student.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(active.getId());
  }

  @Test
  void findByStudentIdOrderByStartDateDesc_returns_full_history_most_recent_first() {
    groupMembershipRepository.save(
        JGroupMembership.builder()
            .studentId(student.getId())
            .groupId(group.getId())
            .startDate(LocalDate.of(2020, 9, 1))
            .endDate(LocalDate.of(2021, 6, 1))
            .build());
    groupMembershipRepository.save(
        JGroupMembership.builder()
            .studentId(student.getId())
            .groupId(group.getId())
            .startDate(LocalDate.of(2021, 9, 1))
            .endDate(null)
            .build());

    var history = groupMembershipRepository.findByStudentIdOrderByStartDateDesc(student.getId());

    assertThat(history).hasSize(2);
    assertThat(history.get(0).getStartDate()).isEqualTo(LocalDate.of(2021, 9, 1));
  }
}
