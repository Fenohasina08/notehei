package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCourseUnit;
import com.example.demo.exception.SemesterValidationException;
import com.example.demo.repository.CourseUnitRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SemesterCreditValidatorTest {

  @Mock private CourseUnitRepository courseUnitRepository;

  private SemesterCreditValidator validator;

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = new SemesterCreditValidator(courseUnitRepository);
    semesterId = UUID.randomUUID();
  }

  private JCourseUnit courseUnit(int credits) {
    return JCourseUnit.builder()
        .id(UUID.randomUUID())
        .credits(credits)
        .semesterId(semesterId)
        .build();
  }

  @Test
  void totalCredits_sums_all_course_units_of_the_semester() {
    when(courseUnitRepository.findBySemesterId(semesterId))
        .thenReturn(List.of(courseUnit(6), courseUnit(4), courseUnit(20)));

    assertThat(validator.totalCredits(semesterId)).isEqualTo(30);
  }

  @Test
  void isComplete_is_true_only_when_total_is_exactly_thirty() {
    when(courseUnitRepository.findBySemesterId(semesterId))
        .thenReturn(List.of(courseUnit(15), courseUnit(15)));
    assertThat(validator.isComplete(semesterId)).isTrue();

    when(courseUnitRepository.findBySemesterId(semesterId)).thenReturn(List.of(courseUnit(20)));
    assertThat(validator.isComplete(semesterId)).isFalse();
  }

  @Test
  void validateDoesNotExceedThirty_rejects_totals_above_thirty() {
    assertThatThrownBy(() -> validator.validateDoesNotExceedThirty(semesterId, 31))
        .isInstanceOf(SemesterValidationException.class);
  }

  @Test
  void validateDoesNotExceedThirty_allows_totals_up_to_and_including_thirty() {
    validator.validateDoesNotExceedThirty(semesterId, 30);
    validator.validateDoesNotExceedThirty(semesterId, 10);
    // no exception thrown = pass
  }
}
