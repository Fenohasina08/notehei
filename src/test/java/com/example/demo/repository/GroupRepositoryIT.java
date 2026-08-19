package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupRepositoryIT extends FacadeIT {

  @Autowired private GroupRepository groupRepository;
  @Autowired private CohortRepository cohortRepository;

  @BeforeEach
  void setUp() {
    groupRepository.deleteAll();
    cohortRepository.deleteAll();
  }

  @Test
  void saves_then_finds_groups_by_cohort() {
    var cohort = cohortRepository.save(JCohort.builder().entryYear(2032).build());
    groupRepository.save(JGroup.builder().reference("G1").cohortId(cohort.getId()).build());
    groupRepository.save(JGroup.builder().reference("G2").cohortId(cohort.getId()).build());

    var found = groupRepository.findByCohortId(cohort.getId());

    assertThat(found)
        .hasSize(2)
        .extracting(JGroup::getReference)
        .containsExactlyInAnyOrder("G1", "G2");
  }

  @Test
  void existsByReferenceAndCohortId_reflects_saved_state() {
    var cohort = cohortRepository.save(JCohort.builder().entryYear(2033).build());

    assertThat(groupRepository.existsByReferenceAndCohortId("G3", cohort.getId())).isFalse();

    groupRepository.save(JGroup.builder().reference("G3").cohortId(cohort.getId()).build());

    assertThat(groupRepository.existsByReferenceAndCohortId("G3", cohort.getId())).isTrue();
  }
}
