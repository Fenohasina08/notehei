package com.example.demo.service;

import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.mapper.GroupMembershipMapper;
import com.example.demo.model.GroupMembership;
import com.example.demo.repository.GroupMembershipRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.validator.GroupMembershipValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GroupMembershipService {

  private final GroupMembershipRepository groupMembershipRepository;
  private final GroupMembershipMapper groupMembershipMapper;
  private final GroupMembershipValidator groupMembershipValidator;
  private final StudentRepository studentRepository;

  @Transactional
  public GroupMembership assignToGroup(UUID studentId, UUID groupId, LocalDate startDate) {
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
    groupMembershipValidator.validateMatriculeFormat(student.getMatricule());
    groupMembershipValidator.validateDates(startDate, null);

    groupMembershipRepository
        .findByStudentIdAndEndDateIsNull(studentId)
        .ifPresent(
            active -> {
              active.setEndDate(startDate);
              groupMembershipRepository.save(active);
            });

    var entity =
        groupMembershipMapper.toEntity(
            new GroupMembership(null, studentId, groupId, startDate, null));
    return groupMembershipMapper.toDto(groupMembershipRepository.save(entity));
  }

  public List<GroupMembership> getHistoryForStudent(UUID studentId) {
    return groupMembershipRepository.findByStudentIdOrderByStartDateDesc(studentId).stream()
        .map(groupMembershipMapper::toDto)
        .toList();
  }

  public List<GroupMembership> getMembersOfGroup(UUID groupId) {
    return groupMembershipRepository.findByGroupId(groupId).stream()
        .map(groupMembershipMapper::toDto)
        .toList();
  }

  public GroupMembership getActiveMembership(UUID studentId) {
    return groupMembershipRepository
        .findByStudentIdAndEndDateIsNull(studentId)
        .map(groupMembershipMapper::toDto)
        .orElse(null);
  }
}
