package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCourseUnit;
import com.example.demo.repository.CourseUnitRepository;
import java.util.List;
import java.util.UUID;
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

  @Test
  void totalCredits_sums_all_course_units_of_the_semester() {

    UUID semesterId = UUID.randomUUID();

    courseUnitRepository.saveAll(
            List.of(
                    JCourseUnit.builder()
                            .id(UUID.randomUUID())
                            .credits(6)
                            .semesterId(semesterId)
                            .build(),

                    JCourseUnit.builder()
                            .id(UUID.randomUUID())
                            .credits(4)
                            .semesterId(semesterId)
                            .build(),

                    JCourseUnit.builder()
                            .id(UUID.randomUUID())
                            .credits(20)
                            .semesterId(semesterId)
                            .build()));

    assertThat(validator.totalCredits(semesterId))
            .isEqualTo(30);
  }

  @Test
  void isComplete_is_true_only_when_total_is_exactly_thirty() {

    UUID semesterId = UUID.randomUUID();

    courseUnitRepository.saveAll(
            List.of(
                    JCourseUnit.builder()
                            .id(UUID.randomUUID())
                            .credits(15)
                            .semesterId(semesterId)
                            .build(),

                    JCourseUnit.builder()
                            .id(UUID.randomUUID())
                            .credits(15)
                            .semesterId(semesterId)
                            .build()));

    assertThat(validator.isComplete(semesterId))
            .isTrue();
  }

  @Test
  void isComplete_is_false_when_total_is_not_thirty() {

    UUID semesterId = UUID.randomUUID();

    courseUnitRepository.save(
            JCourseUnit.builder()
                    .id(UUID.randomUUID())
                    .credits(20)
                    .semesterId(semesterId)
                    .build());

    assertThat(validator.isComplete(semesterId))
            .isFalse();
  }

  @Test
  void validateDoesNotExceedThirty_allows_totals_up_to_thirty() {

    UUID semesterId = UUID.randomUUID();

    validator.validateDoesNotExceedThirty(semesterId, 30);
    validator.validateDoesNotExceedThirty(semesterId, 10);
  }
}