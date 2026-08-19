package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCourseUnit;
import com.example.demo.entity.JCourseUnitCourse;
import com.example.demo.exception.CourseValidationException;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.validator.CourseUnitCourseValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CourseUnitCourseServiceTest {

  @Mock private CourseUnitCourseRepository courseUnitCourseRepository;
  @Mock private CourseUnitRepository courseUnitRepository;

  private CourseUnitCourseService courseUnitCourseService;

  private UUID courseUnitId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    var validator = new CourseUnitCourseValidator(courseUnitRepository, courseUnitCourseRepository);
    courseUnitCourseService = new CourseUnitCourseService(courseUnitCourseRepository, validator);
    courseUnitId = UUID.randomUUID();
  }

  private JCourseUnitCourse link(int credits) {
    return new JCourseUnitCourse(courseUnitId, UUID.randomUUID(), credits);
  }

  @Test
  void attachCourse_rejects_when_it_would_exceed_the_course_units_credits() {
    when(courseUnitRepository.findById(courseUnitId))
        .thenReturn(Optional.of(JCourseUnit.builder().id(courseUnitId).credits(6).build()));
    when(courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)))
        .thenReturn(List.of(link(5)));

    assertThatThrownBy(
            () -> courseUnitCourseService.attachCourse(courseUnitId, UUID.randomUUID(), 2))
        .isInstanceOf(CourseValidationException.class);

    verify(courseUnitCourseRepository, never()).save(any(JCourseUnitCourse.class));
  }

  @Test
  void attachCourse_saves_when_total_stays_within_the_course_units_credits() {
    when(courseUnitRepository.findById(courseUnitId))
        .thenReturn(Optional.of(JCourseUnit.builder().id(courseUnitId).credits(6).build()));
    when(courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)))
        .thenReturn(List.of(link(4)));

    courseUnitCourseService.attachCourse(courseUnitId, UUID.randomUUID(), 2);

    verify(courseUnitCourseRepository).save(any(JCourseUnitCourse.class));
  }
}
