package com.example.demo.service;

import com.example.demo.exception.SemesterNotFoundException;
import com.example.demo.mapper.SemesterMapper;
import com.example.demo.model.Semester;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.validator.SemesterCreditValidator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SemesterService {

  private final SemesterRepository semesterRepository;
  private final SemesterMapper semesterMapper;
  private final SemesterCreditValidator semesterCreditValidator;

  public Semester create(Semester semester) {
    var entity = semesterMapper.toEntity(semester);
    return semesterMapper.toDto(semesterRepository.save(entity));
  }

  public List<Semester> getAll() {
    return semesterRepository.findAll().stream().map(semesterMapper::toDto).toList();
  }

  public Semester getById(UUID id) {
    return semesterRepository
        .findById(id)
        .map(semesterMapper::toDto)
        .orElseThrow(() -> new SemesterNotFoundException(id));
  }

  public boolean isCreditStructureComplete(UUID id) {
    return semesterCreditValidator.isComplete(id);
  }

  public void delete(UUID id) {
    semesterRepository.deleteById(id);
  }
}
