package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JGroup;
import com.example.demo.exception.GroupNotFoundException;
import com.example.demo.exception.GroupValidationException;
import com.example.demo.mapper.StudentGroupMapper;
import com.example.demo.model.StudentGroup;
import com.example.demo.repository.GroupRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StudentGroupServiceTest {

  @Mock private GroupRepository groupRepository;

  private StudentGroupService studentGroupService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    studentGroupService = new StudentGroupService(groupRepository, new StudentGroupMapper());
  }

  @Test
  void create_rejects_a_duplicate_reference_within_the_same_cohort() {
    var cohortId = UUID.randomUUID();
    when(groupRepository.existsByReferenceAndCohortId("G1", cohortId)).thenReturn(true);

    assertThatThrownBy(() -> studentGroupService.create(new StudentGroup(null, "G1", cohortId)))
        .isInstanceOf(GroupValidationException.class);
  }

  @Test
  void create_saves_when_reference_is_unique_within_the_cohort() {
    var cohortId = UUID.randomUUID();
    var id = UUID.randomUUID();
    when(groupRepository.existsByReferenceAndCohortId("G2", cohortId)).thenReturn(false);
    when(groupRepository.save(any(JGroup.class)))
        .thenReturn(JGroup.builder().id(id).reference("G2").cohortId(cohortId).build());

    var result = studentGroupService.create(new StudentGroup(null, "G2", cohortId));

    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  void getById_throws_when_not_found() {
    var id = UUID.randomUUID();
    when(groupRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> studentGroupService.getById(id))
        .isInstanceOf(GroupNotFoundException.class);
  }
}
