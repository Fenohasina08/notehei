package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCourse;
import com.example.demo.exception.CourseValidationException;
import com.example.demo.repository.CourseRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CourseValidatorIT extends FacadeIT {

  @Autowired private CourseRepository courseRepository;

  private CourseValidator courseValidator;

  @BeforeEach
  void setUp() {
    courseValidator = new CourseValidator(courseRepository);
  }

  @Test
  void accepts_course_when_coefficient_is_positive() {
    var course =
        JCourse.builder()
            .reference(uniqueReference())
            .title("Java Programming")
            .coefficient(new BigDecimal("2.00"))
            .build();

    courseValidator.validate(course);
  }

  @Test
  void rejects_course_when_coefficient_is_zero() {
    var course =
        JCourse.builder()
            .reference(uniqueReference())
            .title("Java Programming")
            .coefficient(new BigDecimal("0.00"))
            .build();

    assertThatThrownBy(() -> courseValidator.validate(course))
        .isInstanceOf(CourseValidationException.class)
        .hasMessageContaining("coefficient");
  }

  @Test
  void rejects_course_when_coefficient_is_negative() {
    var course =
        JCourse.builder()
            .reference(uniqueReference())
            .title("Java Programming")
            .coefficient(new BigDecimal("-1.00"))
            .build();

    assertThatThrownBy(() -> courseValidator.validate(course))
        .isInstanceOf(CourseValidationException.class)
        .hasMessageContaining("coefficient");
  }

  @Test
  void rejects_course_when_reference_already_exists() {
    var reference = uniqueReference();

    courseRepository.save(
        JCourse.builder()
            .reference(reference)
            .title("Java Programming")
            .coefficient(new BigDecimal("2.00"))
            .build());

    var course =
        JCourse.builder()
            .reference(reference)
            .title("Advanced Java")
            .coefficient(new BigDecimal("3.00"))
            .build();

    assertThatThrownBy(() -> courseValidator.validate(course))
        .isInstanceOf(CourseValidationException.class)
        .hasMessageContaining("reference");
  }

  private static String uniqueReference() {
    return "JAVA-" + UUID.randomUUID().toString().substring(0, 8);
  }
}
