package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JTeachingAssignment;
import com.example.demo.exception.TeachingAssignmentValidationException;
import com.example.demo.mapper.TeachingAssignmentMapper;
import com.example.demo.model.TeachingAssignment;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TeachingAssignmentServiceTest {

  @Mock private TeachingAssignmentRepository teachingAssignmentRepository;

  private TeachingAssignmentService teachingAssignmentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    teachingAssignmentService =
        new TeachingAssignmentService(teachingAssignmentRepository, new TeachingAssignmentMapper());
  }

  @Test
  void create_rejects_a_duplicate_teacher_course_group_triplet() {
    var teacherId = UUID.randomUUID();
    var courseId = UUID.randomUUID();
    var groupId = UUID.randomUUID();

    when(teachingAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupId(
            teacherId, courseId, groupId))
        .thenReturn(true);

    assertThatThrownBy(
            () ->
                teachingAssignmentService.create(
                    new TeachingAssignment(null, teacherId, courseId, groupId)))
        .isInstanceOf(TeachingAssignmentValidationException.class);
  }

  @Test
  void create_saves_when_triplet_is_unique() {
    var teacherId = UUID.randomUUID();
    var courseId = UUID.randomUUID();
    var groupId = UUID.randomUUID();
    var id = UUID.randomUUID();

    when(teachingAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupId(
            teacherId, courseId, groupId))
        .thenReturn(false);
    when(teachingAssignmentRepository.save(any(JTeachingAssignment.class)))
        .thenReturn(
            JTeachingAssignment.builder()
                .id(id)
                .teacherId(teacherId)
                .courseId(courseId)
                .groupId(groupId)
                .build());

    var result =
        teachingAssignmentService.create(
            new TeachingAssignment(null, teacherId, courseId, groupId));

    assertThat(result.id()).isEqualTo(id);
  }
}
