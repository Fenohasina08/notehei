package com.example.demo.service;

import com.example.demo.exception.AcademicYearNotFoundException;
import com.example.demo.exception.AcademicYearValidationException;
import com.example.demo.mapper.AcademicYearMapper;
import com.example.demo.model.AcademicYear;
import com.example.demo.repository.AcademicYearRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AcademicYearService {

  private final AcademicYearRepository academicYearRepository;
  private final AcademicYearMapper academicYearMapper;

  public AcademicYear create(AcademicYear academicYear) {
    if (academicYear.startYear() == null
        || academicYear.endYear() == null
        || academicYear.endYear() != academicYear.startYear() + 1) {
      throw new AcademicYearValidationException(
          "endYear must be exactly startYear + 1 (got startYear="
              + academicYear.startYear()
              + ", endYear="
              + academicYear.endYear()
              + ")");
    }
    var entity = academicYearMapper.toEntity(academicYear);
    return academicYearMapper.toDto(academicYearRepository.save(entity));
  }

  public List<AcademicYear> getAll() {
    return academicYearRepository.findAll().stream().map(academicYearMapper::toDto).toList();
  }

  public AcademicYear getById(UUID id) {
    return academicYearRepository
        .findById(id)
        .map(academicYearMapper::toDto)
        .orElseThrow(() -> new AcademicYearNotFoundException(id));
  }

  public void delete(UUID id) {
    academicYearRepository.deleteById(id);
  }
}
