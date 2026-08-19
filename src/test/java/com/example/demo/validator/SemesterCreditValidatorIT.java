package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JCourseUnit;
import com.example.demo.entity.JSemester;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.CohortRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.SemesterRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SemesterCreditValidatorIT extends FacadeIT {

  @Autowired private SemesterCreditValidator validator;

  @Autowired private CourseUnitRepository courseUnitRepository;

  @Autowired private CohortRepository cohortRepository;

  @Autowired private AcademicYearRepository academicYearRepository;

  @Autowired private SemesterRepository semesterRepository;

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    JCohort cohort = cohortRepository.save(JCohort.builder().entryYear(2030).build());
    JAcademicYear academicYear =
        academicYearRepository.save(
            JAcademicYear.builder().name("2030-2031").startYear(2030).endYear(2031).build());
    JSemester semester =
        semesterRepository.save(
            JSemester.builder()
                .number(1)
                .cohortId(cohort.getId())
                .academicYearId(academicYear.getId())
                .build());
    semesterId = semester.getId();
  }

  @Test
  void totalCredits_sums_all_course_units_of_the_semester() {

    courseUnitRepository.saveAll(
        List.of(createCourseUnit(6), createCourseUnit(4), createCourseUnit(20)));

    assertThat(validator.totalCredits(semesterId)).isEqualTo(30);
  }

  @Test
  void isComplete_is_true_only_when_total_is_exactly_thirty() {

    courseUnitRepository.saveAll(List.of(createCourseUnit(15), createCourseUnit(15)));

    assertThat(validator.isComplete(semesterId)).isTrue();
  }

  @Test
  void isComplete_is_false_when_total_is_not_thirty() {

    courseUnitRepository.save(createCourseUnit(20));

    assertThat(validator.isComplete(semesterId)).isFalse();
  }

  @Test
  void validateDoesNotExceedThirty_allows_totals_up_to_thirty() {

    validator.validateDoesNotExceedThirty(semesterId, 30);
    validator.validateDoesNotExceedThirty(semesterId, 10);
  }

  private JCourseUnit createCourseUnit(int credits) {
    return JCourseUnit.builder()
        .code("CU-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8))
        .name("Course Unit " + credits + " credits")
        .credits(credits)
        .semesterId(semesterId)
        .build();
  }
}
