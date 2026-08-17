package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.exception.ExamValidationException;
import com.example.demo.repository.ExamRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExamValidatorTest {

  @Mock private ExamRepository examRepository;

  private ExamValidator examValidator;
  private UUID courseId;

  @BeforeEach
  void setUp() {
    examValidator = new ExamValidator(examRepository);
    courseId = UUID.randomUUID();
  }

  @Test
  void accepts_exam_when_weighting_stays_within_100_percent() {
    when(examRepository.findByCourseId(courseId))
        .thenReturn(
            List.of(
                JExam.builder()
                    .courseId(courseId)
                    .type(JExamType.CONTINUOUS_ASSESSMENT)
                    .weighting(bd("40.00"))
                    .build()));

    var newExam =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(bd("60.00"))
            .build();

    examValidator.validateWeighting(newExam);
  }

  @Test
  void rejects_exam_when_non_retake_weighting_would_exceed_100_percent() {
    when(examRepository.findByCourseId(courseId))
        .thenReturn(
            List.of(
                JExam.builder()
                    .courseId(courseId)
                    .type(JExamType.CONTINUOUS_ASSESSMENT)
                    .weighting(bd("70.00"))
                    .build()));

    var newExam =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(bd("40.00"))
            .build();

    assertThatThrownBy(() -> examValidator.validateWeighting(newExam))
        .isInstanceOf(ExamValidationException.class)
        .hasMessageContaining("100%");
  }

  @Test
  void retake_exams_bypass_the_100_percent_rule() {
    var retake =
        JExam.builder().courseId(courseId).type(JExamType.RETAKE).weighting(bd("100.00")).build();

    examValidator.validateWeighting(retake);
  }

  @Test
  void weighting_is_complete_only_when_non_retake_total_equals_100() {
    when(examRepository.findByCourseId(courseId))
        .thenReturn(
            List.of(
                JExam.builder()
                    .courseId(courseId)
                    .type(JExamType.CONTINUOUS_ASSESSMENT)
                    .weighting(bd("30.00"))
                    .build(),
                JExam.builder()
                    .courseId(courseId)
                    .type(JExamType.FINAL_EXAM)
                    .weighting(bd("70.00"))
                    .build()));

    assertThat(examValidator.isCourseWeightingComplete(courseId)).isTrue();
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
