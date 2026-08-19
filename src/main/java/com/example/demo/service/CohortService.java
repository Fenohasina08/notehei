package com.example.demo.service;

import com.example.demo.exception.CohortNotFoundException;
import com.example.demo.exception.CohortValidationException;
import com.example.demo.mapper.CohortMapper;
import com.example.demo.model.Cohort;
import com.example.demo.repository.CohortRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CohortService {

  private final CohortRepository cohortRepository;
  private final CohortMapper cohortMapper;

  public Cohort create(Cohort cohort) {
    if (cohortRepository.existsByEntryYear(cohort.entryYear())) {
      throw new CohortValidationException(
          "A cohort already exists for entry year " + cohort.entryYear());
    }
    var entity = cohortMapper.toEntity(cohort);
    return cohortMapper.toDto(cohortRepository.save(entity));
  }

  public List<Cohort> getAll() {
    return cohortRepository.findAll().stream().map(cohortMapper::toDto).toList();
  }

  public Cohort getById(UUID id) {
    return cohortRepository
        .findById(id)
        .map(cohortMapper::toDto)
        .orElseThrow(() -> new CohortNotFoundException(id));
  }

  public void delete(UUID id) {
    cohortRepository.deleteById(id);
  }
}
