package com.example.demo.service;

import com.example.demo.entity.JGrade;
import com.example.demo.entity.JGradeHistory;
import com.example.demo.mapper.GradeHistoryMapper;
import com.example.demo.model.GradeHistory;
import com.example.demo.repository.GradeHistoryRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.validator.GradeHistoryValidator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GradeHistoryService {

  private final GradeHistoryRepository gradeHistoryRepository;
  private final GradeRepository gradeRepository;
  private final TeacherRepository teacherRepository;
  private final GradeHistoryValidator gradeHistoryValidator;
  private final GradeHistoryMapper gradeHistoryMapper;

  public GradeHistory recordModification(
      UUID gradeId, BigDecimal newValue, String reason, UUID teacherId, UUID adminId) {

    String teacherMatricule = null;

    if (teacherId != null) {
      var teacher =
          teacherRepository
              .findById(teacherId)
              .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

      teacherMatricule = teacher.getMatricule();
    }

    gradeHistoryValidator.validateExactlyOneAuthor(teacherMatricule, adminId);

    JGrade grade =
        gradeRepository
            .findById(gradeId)
            .orElseThrow(() -> new IllegalArgumentException("Note introuvable : " + gradeId));

    var history =
        JGradeHistory.builder()
            .gradeId(gradeId)
            .oldValue(grade.getValue())
            .newValue(newValue)
            .reason(reason)
            .modifiedAt(LocalDateTime.now())
            .teacherMatricule(teacherMatricule)
            .adminId(adminId)
            .build();

    var savedHistory = gradeHistoryRepository.save(history);

    grade.setValue(newValue);
    gradeRepository.save(grade);

    return gradeHistoryMapper.toDto(savedHistory);
  }

  public List<GradeHistory> getHistoryForGrade(UUID gradeId) {
    return gradeHistoryRepository.findByGradeIdOrderByModifiedAtDesc(gradeId).stream()
        .map(gradeHistoryMapper::toDto)
        .toList();
  }
}
