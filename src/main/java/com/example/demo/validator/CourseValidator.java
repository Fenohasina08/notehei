package com.example.demo.validator;

import com.example.demo.entity.JCourse;
import com.example.demo.exception.CourseValidationException;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CourseValidator {

  private static final BigDecimal ZERO = BigDecimal.ZERO;

  public void validate(JCourse course) {
    if (course.getCoefficient() == null || course.getCoefficient().compareTo(ZERO) <= 0) {
      throw new CourseValidationException("The course coefficient must be greater than 0");
    }
  }
}
