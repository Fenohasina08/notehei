package com.example.demo.service;

import com.example.demo.exception.GroupNotFoundException;
import com.example.demo.exception.GroupValidationException;
import com.example.demo.mapper.StudentGroupMapper;
import com.example.demo.model.StudentGroup;
import com.example.demo.repository.GroupRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentGroupService {

  private final GroupRepository groupRepository;
  private final StudentGroupMapper studentGroupMapper;

  public StudentGroup create(StudentGroup group) {
    if (groupRepository.existsByReferenceAndCohortId(group.reference(), group.cohortId())) {
      throw new GroupValidationException(
          "A group with reference "
              + group.reference()
              + " already exists for cohort "
              + group.cohortId());
    }
    var entity = studentGroupMapper.toEntity(group);
    return studentGroupMapper.toDto(groupRepository.save(entity));
  }

  public List<StudentGroup> getAll() {
    return groupRepository.findAll().stream().map(studentGroupMapper::toDto).toList();
  }

  public List<StudentGroup> getByCohort(UUID cohortId) {
    return groupRepository.findByCohortId(cohortId).stream()
        .map(studentGroupMapper::toDto)
        .toList();
  }

  public StudentGroup getById(UUID id) {
    return groupRepository
        .findById(id)
        .map(studentGroupMapper::toDto)
        .orElseThrow(() -> new GroupNotFoundException(id));
  }

  public void delete(UUID id) {
    groupRepository.deleteById(id);
  }
}
