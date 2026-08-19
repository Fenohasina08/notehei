package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCohort;
import com.example.demo.exception.CohortNotFoundException;
import com.example.demo.exception.CohortValidationException;
import com.example.demo.mapper.CohortMapper;
import com.example.demo.model.Cohort;
import com.example.demo.repository.CohortRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CohortServiceTest {

  @Mock private CohortRepository cohortRepository;

  private CohortService cohortService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cohortService = new CohortService(cohortRepository, new CohortMapper());
  }

  @Test
  void create_rejects_a_duplicate_entry_year() {
    when(cohortRepository.existsByEntryYear(2024)).thenReturn(true);

    assertThatThrownBy(() -> cohortService.create(new Cohort(null, 2024)))
        .isInstanceOf(CohortValidationException.class);
  }

  @Test
  void create_saves_when_entry_year_is_unique() {
    when(cohortRepository.existsByEntryYear(2025)).thenReturn(false);
    var id = UUID.randomUUID();
    when(cohortRepository.save(any(JCohort.class)))
        .thenReturn(JCohort.builder().id(id).entryYear(2025).build());

    var result = cohortService.create(new Cohort(null, 2025));

    assertThat(result.id()).isEqualTo(id);
    assertThat(result.entryYear()).isEqualTo(2025);
  }

  @Test
  void getById_throws_when_not_found() {
    var id = UUID.randomUUID();
    when(cohortRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cohortService.getById(id)).isInstanceOf(CohortNotFoundException.class);
  }
}
