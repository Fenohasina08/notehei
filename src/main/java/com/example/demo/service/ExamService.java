package com.example.demo.service;

import com.example.demo.mapper.ExamMapper;
import com.example.demo.model.Exam;
import com.example.demo.repository.ExamRepository;
import com.example.demo.validator.ExamValidator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExamService {

  private final ExamRepository examRepository;
  private final ExamValidator examValidator;
  private final ExamMapper examMapper;

  public Exam createExam(Exam exam) {
    var entity = examMapper.toEntity(exam);
    examValidator.validateWeighting(entity);
    return examMapper.toDto(examRepository.save(entity));
  }

  public List<Exam> getExamsForCourse(UUID courseId) {
    return examRepository.findByCourseId(courseId).stream().map(examMapper::toDto).toList();
  }

  public void deleteExam(UUID examId) {
    examRepository.deleteById(examId);
  }

  public boolean isCourseWeightingComplete(UUID courseId) {
    return examValidator.isCourseWeightingComplete(courseId);
  }
}
