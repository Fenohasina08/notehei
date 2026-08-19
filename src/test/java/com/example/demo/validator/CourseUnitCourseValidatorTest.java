package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCourseUnit;
import com.example.demo.entity.JCourseUnitCourse;
import com.example.demo.exception.CourseUnitNotFoundException;
import com.example.demo.exception.CourseValidationException;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CourseUnitCourseValidatorTest {

  @Mock private CourseUnitRepository courseUnitRepository;
  @Mock private CourseUnitCourseRepository courseUnitCourseRepository;

  private CourseUnitCourseValidator validator;

  private UUID courseUnitId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = new CourseUnitCourseValidator(courseUnitRepository, courseUnitCourseRepository);
    courseUnitId = UUID.randomUUID();
  }

  private JCourseUnitCourse link(int credits) {
    var l = new JCourseUnitCourse();
    l.setCourseUnitId(courseUnitId);
    l.setCourseId(UUID.randomUUID());
    l.setCredits(credits);
    return l;
  }

  @Test
  void validateCreditsMatchCourseUnit_passes_when_sum_equals_course_unit_credits() {
    when(courseUnitRepository.findById(courseUnitId))
        .thenReturn(Optional.of(JCourseUnit.builder().id(courseUnitId).credits(6).build()));
    when(courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)))
        .thenReturn(List.of(link(2), link(4)));

    validator.validateCreditsMatchCourseUnit(courseUnitId);
    // no exception thrown = pass
  }

  @Test
  void validateCreditsMatchCourseUnit_fails_when_sum_differs() {
    when(courseUnitRepository.findById(courseUnitId))
        .thenReturn(Optional.of(JCourseUnit.builder().id(courseUnitId).credits(6).build()));
    when(courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)))
        .thenReturn(List.of(link(2), link(3)));

    assertThatThrownBy(() -> validator.validateCreditsMatchCourseUnit(courseUnitId))
        .isInstanceOf(CourseValidationException.class);
  }

  @Test
  void validateCreditsMatchCourseUnit_fails_when_course_unit_not_found() {
    when(courseUnitRepository.findById(courseUnitId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> validator.validateCreditsMatchCourseUnit(courseUnitId))
        .isInstanceOf(CourseUnitNotFoundException.class);
  }

  @Test
  void validateDoesNotExceedCourseUnitCredits_rejects_overflow() {
    when(courseUnitRepository.findById(courseUnitId))
        .thenReturn(Optional.of(JCourseUnit.builder().id(courseUnitId).credits(6).build()));

    assertThatThrownBy(() -> validator.validateDoesNotExceedCourseUnitCredits(courseUnitId, 7))
        .isInstanceOf(CourseValidationException.class);
  }

  @Test
  void validateDoesNotExceedCourseUnitCredits_allows_up_to_credits() {
    when(courseUnitRepository.findById(courseUnitId))
        .thenReturn(Optional.of(JCourseUnit.builder().id(courseUnitId).credits(6).build()));

    validator.validateDoesNotExceedCourseUnitCredits(courseUnitId, 6);
    // no exception thrown = pass
  }
}
