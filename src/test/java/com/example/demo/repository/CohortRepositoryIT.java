package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCohort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CohortRepositoryIT extends FacadeIT {

  @Autowired private CohortRepository cohortRepository;

  @BeforeEach
  void setUp() {
    cohortRepository.deleteAll();
  }

  @Test
  void saves_then_finds_cohort_by_entry_year() {
    cohortRepository.save(JCohort.builder().entryYear(2030).build());

    var found = cohortRepository.findByEntryYear(2030);

    assertThat(found).isPresent();
    assertThat(found.get().getEntryYear()).isEqualTo(2030);
  }

  @Test
  void existsByEntryYear_reflects_saved_state() {
    assertThat(cohortRepository.existsByEntryYear(2031)).isFalse();

    cohortRepository.save(JCohort.builder().entryYear(2031).build());

    assertThat(cohortRepository.existsByEntryYear(2031)).isTrue();
  }
}
