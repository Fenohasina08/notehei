package com.example.demo.validator;

import com.example.demo.entity.JCourse;
import com.example.demo.exception.CourseValidationException;
import com.example.demo.repository.CourseRepository;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CourseValidator {

  private static final BigDecimal ZERO = BigDecimal.ZERO;

  private final CourseRepository courseRepository;

  public void validate(JCourse course) {
    validateCoefficient(course);
    validateReference(course);
  }

  private void validateCoefficient(JCourse course) {
    if (course.getCoefficient() == null || course.getCoefficient().compareTo(ZERO) <= 0) {
      throw new CourseValidationException("The course coefficient must be greater than 0");
    }
  }

  private void validateReference(JCourse course) {
    if (courseRepository.existsByReference(course.getReference())) {
      throw new CourseValidationException(
          "The course reference already exists: " + course.getReference());
    }
  }
}
