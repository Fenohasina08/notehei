package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JProgram;
import com.example.demo.exception.ProgramNotFoundException;
import com.example.demo.exception.ProgramValidationException;
import com.example.demo.mapper.ProgramMapper;
import com.example.demo.model.Program;
import com.example.demo.repository.ProgramRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProgramServiceTest {

  @Mock private ProgramRepository programRepository;

  private ProgramService programService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    programService = new ProgramService(programRepository, new ProgramMapper());
  }

  @Test
  void create_rejects_a_duplicate_code() {
    when(programRepository.existsByCode("INFO")).thenReturn(true);

    assertThatThrownBy(() -> programService.create(new Program(null, "INFO", "Informatique")))
        .isInstanceOf(ProgramValidationException.class);
  }

  @Test
  void create_saves_when_code_is_unique() {
    var id = UUID.randomUUID();
    when(programRepository.existsByCode("RES")).thenReturn(false);
    when(programRepository.save(any(JProgram.class)))
        .thenReturn(JProgram.builder().id(id).code("RES").name("Réseaux").build());

    var result = programService.create(new Program(null, "RES", "Réseaux"));

    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  void getById_throws_when_not_found() {
    var id = UUID.randomUUID();
    when(programRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> programService.getById(id))
        .isInstanceOf(ProgramNotFoundException.class);
  }
}
