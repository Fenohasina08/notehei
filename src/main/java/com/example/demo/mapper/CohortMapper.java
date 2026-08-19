package com.example.demo.mapper;

import com.example.demo.entity.JCohort;
import com.example.demo.model.Cohort;
import org.springframework.stereotype.Component;

@Component
public class CohortMapper {

  public JCohort toEntity(Cohort dto) {
    return JCohort.builder().id(dto.id()).entryYear(dto.entryYear()).build();
  }

  public Cohort toDto(JCohort entity) {
    return new Cohort(entity.getId(), entity.getEntryYear());
  }
}
