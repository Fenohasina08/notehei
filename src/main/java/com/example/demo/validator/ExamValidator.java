package com.example.demo.validator;

import static com.example.demo.entity.JExamType.RETAKE;

import com.example.demo.entity.JExam;
import com.example.demo.exception.ExamValidationException;
import com.example.demo.repository.ExamRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExamValidator {

  private static final BigDecimal FULL_WEIGHTING = new BigDecimal("100.00");

  private final ExamRepository examRepository;

  public void validateWeighting(JExam exam) {
    if (exam.getType() == RETAKE) {
      return;
    }
    var futureTotal = nonRetakeWeightingTotal(exam.getCourseId()).add(exam.getWeighting());
    if (futureTotal.compareTo(FULL_WEIGHTING) > 0) {
      throw new ExamValidationException(
          "The total weighting of course "
              + exam.getCourseId()
              + " would exceed 100% ("
              + futureTotal
              + ")");
    }
  }

  public boolean isCourseWeightingComplete(UUID courseId) {
    return nonRetakeWeightingTotal(courseId).compareTo(FULL_WEIGHTING) == 0;
  }

  private BigDecimal nonRetakeWeightingTotal(UUID courseId) {
    return examRepository.findByCourseId(courseId).stream()
        .filter(exam -> exam.getType() != RETAKE)
        .map(JExam::getWeighting)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
