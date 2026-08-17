package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JGrade;
import com.example.demo.entity.JGradeHistory;
import com.example.demo.entity.JTeacher;
import com.example.demo.mapper.GradeHistoryMapper;
import com.example.demo.repository.GradeHistoryRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.validator.GradeHistoryValidator;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeHistoryServiceTest {

  @Mock private GradeHistoryRepository gradeHistoryRepository;
  @Mock private GradeRepository gradeRepository;
  @Mock private TeacherRepository teacherRepository;
  @Mock private GradeHistoryValidator gradeHistoryValidator;

  private final GradeHistoryMapper gradeHistoryMapper = new GradeHistoryMapper();

  private GradeHistoryService gradeHistoryService;

  @BeforeEach
  void setUp() {
    gradeHistoryService =
        new GradeHistoryService(
            gradeHistoryRepository,
            gradeRepository,
            teacherRepository,
            gradeHistoryValidator,
            gradeHistoryMapper);
  }

  @Test
  void records_modification_and_updates_the_grade_value() {
    var gradeId = UUID.randomUUID();
    var teacherId = UUID.randomUUID();
    var teacherMatricule = "TCH26183";

    var teacher = JTeacher.builder().id(teacherId).matricule(teacherMatricule).build();

    var grade = JGrade.builder().id(gradeId).value(new BigDecimal("10.00")).build();

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

    var savedHistory =
        JGradeHistory.builder()
            .id(UUID.randomUUID())
            .gradeId(gradeId)
            .oldValue(new BigDecimal("10.00"))
            .newValue(new BigDecimal("14.00"))
            .teacherMatricule(teacherMatricule)
            .build();

    when(gradeHistoryRepository.save(any(JGradeHistory.class))).thenReturn(savedHistory);

    var result =
        gradeHistoryService.recordModification(
            gradeId, new BigDecimal("14.00"), "erreur de saisie", teacherId, null);

    assertThat(result.oldValue()).isEqualByComparingTo("10.00");

    assertThat(result.newValue()).isEqualByComparingTo("14.00");

    assertThat(result.teacherMatricule()).isEqualTo(teacherMatricule);

    verify(teacherRepository).findById(teacherId);

    verify(gradeHistoryValidator).validateExactlyOneAuthor(teacherMatricule, null);

    verify(gradeRepository).save(grade);

    assertThat(grade.getValue()).isEqualByComparingTo("14.00");
  }
}
