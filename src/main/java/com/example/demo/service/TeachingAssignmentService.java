package com.example.demo.service;

import com.example.demo.exception.TeachingAssignmentNotFoundException;
import com.example.demo.exception.TeachingAssignmentValidationException;
import com.example.demo.mapper.TeachingAssignmentMapper;
import com.example.demo.model.TeachingAssignment;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeachingAssignmentService {

  private final TeachingAssignmentRepository teachingAssignmentRepository;
  private final TeachingAssignmentMapper teachingAssignmentMapper;

  public TeachingAssignment create(TeachingAssignment assignment) {
    if (teachingAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupId(
        assignment.teacherId(), assignment.courseId(), assignment.groupId())) {
      throw new TeachingAssignmentValidationException(
          "Teacher "
              + assignment.teacherId()
              + " is already assigned to course "
              + assignment.courseId()
              + " for group "
              + assignment.groupId());
    }
    var entity = teachingAssignmentMapper.toEntity(assignment);
    return teachingAssignmentMapper.toDto(teachingAssignmentRepository.save(entity));
  }

  public List<TeachingAssignment> getAll() {
    return teachingAssignmentRepository.findAll().stream()
        .map(teachingAssignmentMapper::toDto)
        .toList();
  }

  public List<TeachingAssignment> getForTeacher(UUID teacherId) {
    return teachingAssignmentRepository.findByTeacherId(teacherId).stream()
        .map(teachingAssignmentMapper::toDto)
        .toList();
  }

  public TeachingAssignment getById(UUID id) {
    return teachingAssignmentRepository
        .findById(id)
        .map(teachingAssignmentMapper::toDto)
        .orElseThrow(() -> new TeachingAssignmentNotFoundException(id));
  }

  public boolean teacherHasAssignmentForCourse(UUID teacherId, UUID courseId) {
    return teachingAssignmentRepository.existsByTeacherIdAndCourseId(teacherId, courseId);
  }

  public void delete(UUID id) {
    teachingAssignmentRepository.deleteById(id);
  }
}
