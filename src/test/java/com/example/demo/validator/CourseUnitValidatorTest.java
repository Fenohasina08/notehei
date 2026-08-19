package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCourseUnitCourse;
import com.example.demo.exception.CourseUnitValidationException;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitProgramRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CourseUnitValidatorTest {

  @Mock private CourseUnitProgramRepository courseUnitProgramRepository;
  @Mock private CourseUnitCourseRepository courseUnitCourseRepository;

  private CourseUnitValidator validator;

  private UUID courseUnitId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = new CourseUnitValidator(courseUnitProgramRepository, courseUnitCourseRepository);
    courseUnitId = UUID.randomUUID();
  }

  @Test
  void validateIsComplete_fails_when_no_program_attached() {
    when(courseUnitProgramRepository.existsByCourseUnitId(courseUnitId)).thenReturn(false);

    assertThatThrownBy(() -> validator.validateIsComplete(courseUnitId))
        .isInstanceOf(CourseUnitValidationException.class)
        .hasMessageContaining("program");
  }

  @Test
  void validateIsComplete_fails_when_no_course_attached() {
    when(courseUnitProgramRepository.existsByCourseUnitId(courseUnitId)).thenReturn(true);
    when(courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> validator.validateIsComplete(courseUnitId))
        .isInstanceOf(CourseUnitValidationException.class)
        .hasMessageContaining("course");
  }

  @Test
  void validateIsComplete_passes_when_both_program_and_course_attached() {
    when(courseUnitProgramRepository.existsByCourseUnitId(courseUnitId)).thenReturn(true);
    when(courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)))
        .thenReturn(List.of(new JCourseUnitCourse(courseUnitId, UUID.randomUUID(), 5)));

    validator.validateIsComplete(courseUnitId);
    // no exception thrown = pass
  }
}
