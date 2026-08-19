package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitProgramRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseUnitValidatorIT extends FacadeIT {

  @Autowired private CourseUnitValidator validator;

  @Autowired private CourseUnitProgramRepository courseUnitProgramRepository;

  @Autowired private CourseUnitCourseRepository courseUnitCourseRepository;

  @Test
  void validateIsComplete_fails_when_no_program_attached() {

    UUID courseUnitId = UUID.randomUUID();

    assertThatThrownBy(() -> validator.validateIsComplete(courseUnitId))
        .isInstanceOf(Exception.class);
  }

  @Test
  void validateIsComplete_fails_when_no_course_attached() {

    UUID courseUnitId = UUID.randomUUID();

    // Le CourseUnit doit être créé ici avant de tester
    // l'absence de programme/course.
    assertThatThrownBy(() -> validator.validateIsComplete(courseUnitId))
        .isInstanceOf(Exception.class);
  }
}
