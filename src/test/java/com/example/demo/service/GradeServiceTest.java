package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.entity.JGrade;
import com.example.demo.mapper.GradeMapper;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.validator.GradeValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

  @Mock private GradeRepository gradeRepository;
  @Mock private ExamRepository examRepository;
  @Mock private GradeValidator gradeValidator;

  private final GradeMapper gradeMapper = new GradeMapper();

  private GradeService gradeService;
  private UUID studentId;
  private UUID courseId;

  @BeforeEach
  void setUp() {
    gradeService = new GradeService(gradeRepository, examRepository, gradeValidator, gradeMapper);
    studentId = UUID.randomUUID();
    courseId = UUID.randomUUID();
  }

  @Test
  void computes_weighted_average_without_retake() {
    var ccExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.CONTINUOUS_ASSESSMENT)
            .weighting(bd("30.00"))
            .build();
    var finalExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(bd("70.00"))
            .build();
    when(examRepository.findByCourseId(courseId)).thenReturn(List.of(ccExam, finalExam));

    when(gradeRepository.findByStudentIdAndExamId(studentId, ccExam.getId()))
        .thenReturn(Optional.of(gradeOf(bd("10.00"))));
    when(gradeRepository.findByStudentIdAndExamId(studentId, finalExam.getId()))
        .thenReturn(Optional.of(gradeOf(bd("16.00"))));

    var result = gradeService.computeRetainedGrade(studentId, courseId);

    assertThat(result).isEqualByComparingTo("14.2000");
  }

  @Test
  void retained_grade_is_max_of_normal_total_and_retake_when_retake_exists() {
    var finalExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(bd("100.00"))
            .build();
    var retakeExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.RETAKE)
            .weighting(bd("100.00"))
            .build();
    when(examRepository.findByCourseId(courseId)).thenReturn(List.of(finalExam, retakeExam));

    when(gradeRepository.findByStudentIdAndExamId(studentId, finalExam.getId()))
        .thenReturn(Optional.of(gradeOf(bd("8.00"))));
    when(gradeRepository.findByStudentIdAndExamId(studentId, retakeExam.getId()))
        .thenReturn(Optional.of(gradeOf(bd("13.00"))));

    var result = gradeService.computeRetainedGrade(studentId, courseId);

    assertThat(result).isEqualByComparingTo("13.00");
  }

  @Test
  void normal_total_is_kept_when_retake_grade_does_not_exist() {
    var finalExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.FINAL_EXAM)
            .weighting(bd("100.00"))
            .build();
    var retakeExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .courseId(courseId)
            .type(JExamType.RETAKE)
            .weighting(bd("100.00"))
            .build();
    when(examRepository.findByCourseId(courseId)).thenReturn(List.of(finalExam, retakeExam));

    when(gradeRepository.findByStudentIdAndExamId(studentId, finalExam.getId()))
        .thenReturn(Optional.of(gradeOf(bd("15.00"))));
    when(gradeRepository.findByStudentIdAndExamId(studentId, retakeExam.getId()))
        .thenReturn(Optional.empty());

    var result = gradeService.computeRetainedGrade(studentId, courseId);

    assertThat(result).isEqualByComparingTo("15.00");
  }

  private static JGrade gradeOf(BigDecimal value) {
    return JGrade.builder().id(UUID.randomUUID()).value(value).build();
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
