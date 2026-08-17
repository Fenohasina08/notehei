package com.example.demo.service;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.mapper.GradeMapper;
import com.example.demo.model.Grade;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.validator.GradeValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GradeService {

  private final GradeRepository gradeRepository;
  private final ExamRepository examRepository;
  private final TeacherRepository teacherRepository;
  private final GradeValidator gradeValidator;
  private final GradeMapper gradeMapper;

  public Grade createGradeByTeacher(Grade grade, UUID teacherId) {
    gradeValidator.validateTeacherOwnsExam(teacherId, grade.examId());

    var teacher =
        teacherRepository
            .findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

    var entity = gradeMapper.toEntity(grade);
    entity.setTeacherMatricule(teacher.getMatricule());
    entity.setAdminId(null);
    entity.setEnteredAt(LocalDateTime.now());

    gradeValidator.validateExactlyOneAuthor(entity.getTeacherMatricule(), entity.getAdminId());

    return gradeMapper.toDto(gradeRepository.save(entity));
  }

  public Grade createGradeByAdmin(Grade grade, UUID adminId) {
    var entity = gradeMapper.toEntity(grade);
    entity.setAdminId(adminId);
    entity.setTeacherMatricule(null);
    entity.setEnteredAt(LocalDateTime.now());

    gradeValidator.validateExactlyOneAuthor(entity.getTeacherMatricule(), entity.getAdminId());

    return gradeMapper.toDto(gradeRepository.save(entity));
  }

  public List<Grade> getGradesForStudent(String studentMatricule) {
    return gradeRepository.findByStudentMatricule(studentMatricule).stream()
        .map(gradeMapper::toDto)
        .toList();
  }

  public BigDecimal computeRetainedGrade(String studentMatricule, UUID courseId) {
    var exams = examRepository.findByCourseId(courseId);

    var normalTotal =
        exams.stream()
            .filter(exam -> exam.getType() != JExamType.RETAKE)
            .map(exam -> weightedValue(studentMatricule, exam))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    var retakeExam = exams.stream().filter(exam -> exam.getType() == JExamType.RETAKE).findFirst();

    if (retakeExam.isEmpty()) {
      return normalTotal;
    }

    var retakeGrade =
        gradeRepository.findByStudentMatriculeAndExamId(studentMatricule, retakeExam.get().getId());

    if (retakeGrade.isEmpty()) {
      return normalTotal;
    }

    return normalTotal.max(retakeGrade.get().getValue());
  }

  private BigDecimal weightedValue(String studentMatricule, JExam exam) {
    return gradeRepository
        .findByStudentMatriculeAndExamId(studentMatricule, exam.getId())
        .map(
            grade ->
                grade
                    .getValue()
                    .multiply(exam.getWeighting())
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
        .orElse(BigDecimal.ZERO);
  }
}
