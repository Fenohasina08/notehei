package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JGroupMembership;
import com.example.demo.entity.JStudent;
import com.example.demo.mapper.GroupMembershipMapper;
import com.example.demo.repository.GroupMembershipRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.validator.GroupMembershipValidator;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GroupMembershipServiceTest {

  @Mock private GroupMembershipRepository groupMembershipRepository;
  @Mock private StudentRepository studentRepository;

  private GroupMembershipMapper groupMembershipMapper;
  private GroupMembershipValidator groupMembershipValidator;

  private GroupMembershipService groupMembershipService;

  private UUID studentId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    groupMembershipMapper = new GroupMembershipMapper();
    groupMembershipValidator = new GroupMembershipValidator();
    groupMembershipService =
        new GroupMembershipService(
            groupMembershipRepository,
            groupMembershipMapper,
            groupMembershipValidator,
            studentRepository);

    studentId = UUID.randomUUID();

    var student = new JStudent();
    student.setId(studentId);
    student.setMatricule("STD25007");
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(groupMembershipRepository.save(any(JGroupMembership.class)))
        .thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void assignToGroup_closes_previous_active_membership_before_opening_new_one() {
    UUID oldGroupId = UUID.randomUUID();
    UUID newGroupId = UUID.randomUUID();
    var startDate = LocalDate.of(2025, 9, 1);

    var activeMembership =
        JGroupMembership.builder()
            .id(UUID.randomUUID())
            .studentId(studentId)
            .groupId(oldGroupId)
            .startDate(LocalDate.of(2024, 9, 1))
            .endDate(null)
            .build();

    when(groupMembershipRepository.findByStudentIdAndEndDateIsNull(studentId))
        .thenReturn(Optional.of(activeMembership));

    groupMembershipService.assignToGroup(studentId, newGroupId, startDate);

    // The previous active membership gets closed (endDate = new startDate) — this is what
    // also covers a "redoublement": a student can move to a new group after their old one closes.
    assertThat(activeMembership.getEndDate()).isEqualTo(startDate);
    verify(groupMembershipRepository, times(2)).save(any(JGroupMembership.class));
  }

  @Test
  void assignToGroup_does_not_try_to_close_anything_when_no_active_membership_exists() {
    when(groupMembershipRepository.findByStudentIdAndEndDateIsNull(studentId))
        .thenReturn(Optional.empty());

    groupMembershipService.assignToGroup(studentId, UUID.randomUUID(), LocalDate.of(2025, 9, 1));

    ArgumentCaptor<JGroupMembership> captor = ArgumentCaptor.forClass(JGroupMembership.class);
    verify(groupMembershipRepository, times(1)).save(captor.capture());
    assertThat(captor.getValue().getEndDate()).isNull();
  }

  @Test
  void assignToGroup_rejects_invalid_matricule_before_touching_the_repository() {
    var student = new JStudent();
    student.setId(studentId);
    student.setMatricule("NOT-A-MATRICULE");
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

    try {
      groupMembershipService.assignToGroup(studentId, UUID.randomUUID(), LocalDate.of(2025, 9, 1));
      org.junit.jupiter.api.Assertions.fail("Expected a validation exception");
    } catch (Exception expected) {
      // expected
    }

    verify(groupMembershipRepository, never()).save(any(JGroupMembership.class));
  }
}
