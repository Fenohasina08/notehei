package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JCourseUnit;
import com.example.demo.entity.JSemester;
import com.example.demo.exception.SemesterNotFoundException;
import com.example.demo.mapper.SemesterMapper;
import com.example.demo.model.Semester;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.validator.SemesterCreditValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SemesterServiceTest {

  @Mock private SemesterRepository semesterRepository;
  @Mock private CourseUnitRepository courseUnitRepository;

  private SemesterService semesterService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    semesterService =
        new SemesterService(
            semesterRepository,
            new SemesterMapper(),
            new SemesterCreditValidator(courseUnitRepository));
  }

  @Test
  void create_saves_and_returns_the_semester() {
    var cohortId = UUID.randomUUID();
    var academicYearId = UUID.randomUUID();
    var id = UUID.randomUUID();

    when(semesterRepository.save(any(JSemester.class)))
        .thenReturn(
            JSemester.builder()
                .id(id)
                .number(1)
                .cohortId(cohortId)
                .academicYearId(academicYearId)
                .build());

    var result = semesterService.create(new Semester(null, 1, cohortId, academicYearId));

    assertThat(result.id()).isEqualTo(id);
    assertThat(result.number()).isEqualTo(1);
  }

  @Test
  void getById_throws_when_not_found() {
    var id = UUID.randomUUID();
    when(semesterRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> semesterService.getById(id))
        .isInstanceOf(SemesterNotFoundException.class);
  }

  @Test
  void isCreditStructureComplete_delegates_to_the_validator() {
    var semesterId = UUID.randomUUID();
    when(courseUnitRepository.findBySemesterId(semesterId))
        .thenReturn(List.of(JCourseUnit.builder().credits(30).build()));

    assertThat(semesterService.isCreditStructureComplete(semesterId)).isTrue();
  }
}
