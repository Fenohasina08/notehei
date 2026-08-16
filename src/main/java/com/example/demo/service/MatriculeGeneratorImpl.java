package com.example.demo.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatriculeGeneratorImpl implements MatriculeGenerator {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public String generateStudentMatricule() {
    return generate("STD");
  }

  @Override
  public String generateTeacherMatricule() {
    return generate("TCH");
  }

  private String generate(String prefix) {
    Long sequenceValue =
        jdbcTemplate.queryForObject("SELECT nextval('user_matricule_seq')", Long.class);

    int year = LocalDate.now().getYear() % 100;

    return String.format("%s%03d%d", prefix, year, sequenceValue);
  }
}
