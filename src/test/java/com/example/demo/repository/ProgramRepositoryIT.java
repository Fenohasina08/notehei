package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JProgram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProgramRepositoryIT extends FacadeIT {

  @Autowired private ProgramRepository programRepository;

  @BeforeEach
  void setUp() {
    programRepository.deleteAll();
  }

  @Test
  void saves_then_retrieves_program() {
    var saved =
        programRepository.save(JProgram.builder().code("INFO").name("Informatique").build());

    var found = programRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getCode()).isEqualTo("INFO");
  }

  @Test
  void existsByCode_reflects_saved_state() {
    assertThat(programRepository.existsByCode("RES")).isFalse();

    programRepository.save(JProgram.builder().code("RES").name("Réseaux").build());

    assertThat(programRepository.existsByCode("RES")).isTrue();
  }
}
