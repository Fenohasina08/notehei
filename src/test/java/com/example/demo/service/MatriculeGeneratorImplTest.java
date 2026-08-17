package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class MatriculeGeneratorImplTest {

  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private MatriculeGeneratorImpl matriculeGenerator;

  @Test
  void generateStudentMatricule_shouldReturnStudentMatricule() {

    when(jdbcTemplate.queryForObject("SELECT nextval('user_matricule_seq')", Long.class))
        .thenReturn(182L);

    String matricule = matriculeGenerator.generateStudentMatricule();

    String year = String.format("%02d", LocalDate.now().getYear() % 100);

    assertEquals("STD" + year + "182", matricule);

    verify(jdbcTemplate).queryForObject("SELECT nextval('user_matricule_seq')", Long.class);
  }

  @Test
  void generateTeacherMatricule_shouldReturnTeacherMatricule() {

    when(jdbcTemplate.queryForObject("SELECT nextval('user_matricule_seq')", Long.class))
        .thenReturn(183L);

    String matricule = matriculeGenerator.generateTeacherMatricule();

    String year = String.format("%02d", LocalDate.now().getYear() % 100);

    assertEquals("TCH" + year + "183", matricule);

    verify(jdbcTemplate).queryForObject("SELECT nextval('user_matricule_seq')", Long.class);
  }
}
