package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.exception.ExamValidationException;
import com.example.demo.mapper.ExamMapper;
import com.example.demo.model.Exam;
import com.example.demo.model.ExamType;
import com.example.demo.repository.ExamRepository;
import com.example.demo.validator.ExamValidator;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

  @Mock private ExamRepository examRepository;
  @Mock private ExamValidator examValidator;

  private final ExamMapper examMapper = new ExamMapper();

  private ExamService examService;

  @BeforeEach
  void setUp() {
    examService = new ExamService(examRepository, examValidator, examMapper);
  }

  @Test
  void creates_exam_after_validation_passes() {
    var courseId = UUID.randomUUID();
    var dto = new Exam(null, courseId, ExamType.FINAL_EXAM, null, new BigDecimal("60.00"));
    var saved =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(new BigDecimal("60.00"))
            .build();
    when(examRepository.save(any(JExam.class))).thenReturn(saved);

    var result = examService.createExam(dto);

    assertThat(result.id()).isEqualTo(saved.getId());
    assertThat(result.type()).isEqualTo(ExamType.FINAL_EXAM);
    verify(examValidator).validateWeighting(any(JExam.class));
  }

  @Test
  void propagates_validation_exception_without_saving() {
    var courseId = UUID.randomUUID();
    var dto = new Exam(null, courseId, ExamType.FINAL_EXAM, null, new BigDecimal("60.00"));
    org.mockito.Mockito.doThrow(new ExamValidationException("limit exceeded"))
        .when(examValidator)
        .validateWeighting(any(JExam.class));

    assertThatThrownBy(() -> examService.createExam(dto))
        .isInstanceOf(ExamValidationException.class);

    verifyNoInteractions(examRepository);
  }
}
