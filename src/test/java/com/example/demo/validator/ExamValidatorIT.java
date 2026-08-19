package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.exception.ExamValidationException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import java.math.BigDecimal;
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
class ExamValidatorIT extends FacadeIT {

  @Autowired private ExamValidator examValidator;

  @Autowired private ExamRepository examRepository;

  @Autowired private CourseRepository courseRepository;

  private UUID courseId;

  @BeforeEach
  void setUp() {
    JCourse course =
        JCourse.builder()
            .reference("EXAM-VAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8))
            .title("Exam Validator Test Course")
            .coefficient(new BigDecimal("1.00"))
            .build();
    course = courseRepository.save(course);
    courseId = course.getId();
  }

  @Test
  void accepts_exam_when_weighting_stays_within_100_percent() {

    examRepository.save(
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.CONTINUOUS_ASSESSMENT)
            .weighting(new BigDecimal("40.00"))
            .build());

    var newExam =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(new BigDecimal("60.00"))
            .build();

    examValidator.validateWeighting(newExam);
  }

  @Test
  void rejects_exam_when_non_retake_weighting_would_exceed_100_percent() {

    examRepository.save(
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.CONTINUOUS_ASSESSMENT)
            .weighting(new BigDecimal("70.00"))
            .build());

    var newExam =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(new BigDecimal("40.00"))
            .build();

    assertThatThrownBy(() -> examValidator.validateWeighting(newExam))
        .isInstanceOf(ExamValidationException.class)
        .hasMessageContaining("100%");
  }

  @Test
  void retake_exams_bypass_the_100_percent_rule() {

    var retake =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.RETAKE)
            .weighting(new BigDecimal("100.00"))
            .build();

    examValidator.validateWeighting(retake);
  }

  @Test
  void weighting_is_complete_only_when_non_retake_total_equals_100() {

    examRepository.save(
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.CONTINUOUS_ASSESSMENT)
            .weighting(new BigDecimal("30.00"))
            .build());

    examRepository.save(
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(new BigDecimal("70.00"))
            .build());

    assertThat(examValidator.isCourseWeightingComplete(courseId)).isTrue();
  }
}
