package com.example.demo.service;

import com.example.demo.entity.JCourseUnitProgram;
import com.example.demo.entity.JCourseUnitProgramId;
import com.example.demo.exception.CourseUnitNotFoundException;
import com.example.demo.mapper.CourseUnitMapper;
import com.example.demo.model.CourseUnit;
import com.example.demo.repository.CourseUnitProgramRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.validator.CourseUnitValidator;
import com.example.demo.validator.SemesterCreditValidator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseUnitService {

  private final CourseUnitRepository courseUnitRepository;
  private final CourseUnitMapper courseUnitMapper;
  private final CourseUnitProgramRepository courseUnitProgramRepository;
  private final SemesterCreditValidator semesterCreditValidator;
  private final CourseUnitValidator courseUnitValidator;

  public CourseUnit create(CourseUnit courseUnit) {
    int futureTotal =
        semesterCreditValidator.totalCredits(courseUnit.semesterId()) + courseUnit.credits();
    semesterCreditValidator.validateDoesNotExceedThirty(courseUnit.semesterId(), futureTotal);

    var entity = courseUnitMapper.toEntity(courseUnit);
    return courseUnitMapper.toDto(courseUnitRepository.save(entity));
  }

  public List<CourseUnit> getBySemester(UUID semesterId) {
    return courseUnitRepository.findBySemesterId(semesterId).stream()
        .map(courseUnitMapper::toDto)
        .toList();
  }

  public CourseUnit getById(UUID id) {
    return courseUnitRepository
        .findById(id)
        .map(courseUnitMapper::toDto)
        .orElseThrow(() -> new CourseUnitNotFoundException(id));
  }

  public void attachProgram(UUID courseUnitId, UUID programId) {
    courseUnitProgramRepository.save(new JCourseUnitProgram(courseUnitId, programId));
  }

  public void detachProgram(UUID courseUnitId, UUID programId) {
    courseUnitProgramRepository.deleteById(new JCourseUnitProgramId(courseUnitId, programId));
  }

  /** Throws if the course unit is missing a program or a course attachment. */
  public void validateComplete(UUID courseUnitId) {
    courseUnitValidator.validateIsComplete(courseUnitId);
  }

  public void delete(UUID id) {
    courseUnitRepository.deleteById(id);
  }
}
