package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JAcademicYear;
import com.example.demo.exception.AcademicYearNotFoundException;
import com.example.demo.exception.AcademicYearValidationException;
import com.example.demo.mapper.AcademicYearMapper;
import com.example.demo.model.AcademicYear;
import com.example.demo.repository.AcademicYearRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AcademicYearServiceTest {

  @Mock private AcademicYearRepository academicYearRepository;

  private AcademicYearService academicYearService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    academicYearService = new AcademicYearService(academicYearRepository, new AcademicYearMapper());
  }

  @Test
  void create_rejects_when_endYear_is_not_startYear_plus_one() {
    var invalid = new AcademicYear(null, "2024-2026", 2024, 2026);

    assertThatThrownBy(() -> academicYearService.create(invalid))
        .isInstanceOf(AcademicYearValidationException.class);
  }

  @Test
  void create_rejects_when_years_are_missing() {
    assertThatThrownBy(() -> academicYearService.create(new AcademicYear(null, "x", null, 2025)))
        .isInstanceOf(AcademicYearValidationException.class);
  }

  @Test
  void create_saves_when_endYear_equals_startYear_plus_one() {
    var id = UUID.randomUUID();
    when(academicYearRepository.save(any(JAcademicYear.class)))
        .thenReturn(
            JAcademicYear.builder().id(id).name("2024-2025").startYear(2024).endYear(2025).build());

    var result = academicYearService.create(new AcademicYear(null, "2024-2025", 2024, 2025));

    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  void getById_throws_when_not_found() {
    var id = UUID.randomUUID();
    when(academicYearRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> academicYearService.getById(id))
        .isInstanceOf(AcademicYearNotFoundException.class);
  }
}
