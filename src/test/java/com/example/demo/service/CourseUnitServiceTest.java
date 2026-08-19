package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCourseUnit;
import com.example.demo.exception.SemesterValidationException;
import com.example.demo.mapper.CourseUnitMapper;
import com.example.demo.model.CourseUnit;
import com.example.demo.repository.CourseUnitProgramRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.validator.CourseUnitValidator;
import com.example.demo.validator.SemesterCreditValidator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CourseUnitServiceTest {

  @Mock private CourseUnitRepository courseUnitRepository;
  @Mock private CourseUnitProgramRepository courseUnitProgramRepository;

  private CourseUnitService courseUnitService;

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    var semesterCreditValidator = new SemesterCreditValidator(courseUnitRepository);
    var courseUnitValidator =
        new CourseUnitValidator(
            courseUnitProgramRepository,
            org.mockito.Mockito.mock(com.example.demo.repository.CourseUnitCourseRepository.class));
    courseUnitService =
        new CourseUnitService(
            courseUnitRepository,
            new CourseUnitMapper(),
            courseUnitProgramRepository,
            semesterCreditValidator,
            courseUnitValidator);
    semesterId = UUID.randomUUID();
  }

  @Test
  void create_rejects_a_course_unit_that_would_push_semester_total_above_thirty() {
    when(courseUnitRepository.findBySemesterId(semesterId))
        .thenReturn(List.of(JCourseUnit.builder().id(UUID.randomUUID()).credits(25).build()));

    var newCourseUnit = new CourseUnit(null, "UE-X", "Excess UE", 6, semesterId);

    assertThatThrownBy(() -> courseUnitService.create(newCourseUnit))
        .isInstanceOf(SemesterValidationException.class);
  }

  @Test
  void create_saves_when_total_stays_at_or_under_thirty() {
    when(courseUnitRepository.findBySemesterId(semesterId))
        .thenReturn(List.of(JCourseUnit.builder().id(UUID.randomUUID()).credits(24).build()));
    when(courseUnitRepository.save(any(JCourseUnit.class)))
        .thenAnswer(
            i -> {
              JCourseUnit arg = i.getArgument(0);
              arg.setId(UUID.randomUUID());
              return arg;
            });

    var newCourseUnit = new CourseUnit(null, "UE-Y", "Fits exactly", 6, semesterId);

    var result = courseUnitService.create(newCourseUnit);

    assertThat(result.credits()).isEqualTo(6);
  }
}
