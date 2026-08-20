package com.example.demo.service;

import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import com.example.demo.mapper.GradeMapper;
import com.example.demo.model.CourseGrade;
import com.example.demo.model.CourseUnitAverage;
import com.example.demo.model.Grade;
import com.example.demo.model.MissingGrade;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.GroupMembershipRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import com.example.demo.validator.GradeValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GradeService {

  private final GradeRepository gradeRepository;
  private final ExamRepository examRepository;
  private final TeacherRepository teacherRepository;
  private final CourseUnitRepository courseUnitRepository;
  private final CourseUnitCourseRepository courseUnitCourseRepository;
  private final TeachingAssignmentRepository teachingAssignmentRepository;
  private final GroupMembershipRepository groupMembershipRepository;
  private final StudentRepository studentRepository;
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

  public List<MissingGrade> getStudentsMissingGradeForExam(UUID teacherId, UUID examId) {
    var exam = examRepository.findById(examId).orElseThrow();

    var groupIds =
        teachingAssignmentRepository.findByTeacherId(teacherId).stream()
            .filter(ta -> ta.getCourseId().equals(exam.getCourseId()))
            .map(ta -> ta.getGroupId())
            .toList();

    var alreadyGraded =
        gradeRepository.findByExamId(examId).stream()
            .map(grade -> grade.getStudentMatricule())
            .collect(java.util.stream.Collectors.toSet());

    Set<UUID> studentIdsInScope = new java.util.HashSet<>();
    for (var groupId : groupIds) {
      groupMembershipRepository.findByGroupId(groupId).stream()
          .filter(m -> m.getEndDate() == null)
          .forEach(m -> studentIdsInScope.add(m.getStudentId()));
    }

    return studentIdsInScope.stream()
        .map(studentRepository::findById)
        .flatMap(java.util.Optional::stream)
        .filter(student -> !alreadyGraded.contains(student.getMatricule()))
        .map(
            student ->
                new MissingGrade(
                    student.getId(),
                    student.getMatricule(),
                    student.getFirstName() + " " + student.getLastName()))
        .toList();
  }

  /**
   * Retained grade for a single course: sum of weighted exam grades, or the retake grade if higher.
   */
  public BigDecimal computeRetainedGrade(String studentMatricule, UUID courseId) {
    var exams = examRepository.findByCourseId(courseId);

    var normalTotal =
        exams.stream()
            .filter(exam -> exam.getType() != JExamType.RETAKE)
            .map(exam -> weightedExamValue(studentMatricule, exam))
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

  /**
   * Retained grade for every course belonging to the given course unit, with the credits used to
   * weight it within that unit.
   */
  public List<CourseGrade> getCourseGradesForCourseUnit(
      String studentMatricule, UUID courseUnitId) {
    return courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)).stream()
        .map(
            link ->
                new CourseGrade(
                    link.getCourseId(),
                    link.getCredits(),
                    computeRetainedGrade(studentMatricule, link.getCourseId())))
        .toList();
  }

  /** Course unit average: courses weighted by their credits within that unit. */
  public BigDecimal computeCourseUnitAverage(String studentMatricule, UUID courseUnitId) {
    var courseGrades = getCourseGradesForCourseUnit(studentMatricule, courseUnitId);
    return weightedAverageOfCourseGrades(courseGrades);
  }

  /**
   * Average for every course unit in the given semester, with the credits used to weight it within
   * the semester.
   */
  public List<CourseUnitAverage> getCourseUnitAveragesForSemester(
      String studentMatricule, UUID semesterId) {
    return courseUnitRepository.findBySemesterId(semesterId).stream()
        .map(
            courseUnit ->
                new CourseUnitAverage(
                    courseUnit.getId(),
                    courseUnit.getCredits(),
                    computeCourseUnitAverage(studentMatricule, courseUnit.getId())))
        .toList();
  }

  /** Semester average: course units weighted by their credits within the semester. */
  public BigDecimal computeSemesterAverage(String studentMatricule, UUID semesterId) {
    var courseUnitAverages = getCourseUnitAveragesForSemester(studentMatricule, semesterId);
    return weightedAverageOfCourseUnitAverages(courseUnitAverages);
  }

  private BigDecimal weightedAverageOfCourseGrades(List<CourseGrade> courseGrades) {
    var totalCredits =
        courseGrades.stream()
            .map(cg -> BigDecimal.valueOf(cg.credits()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    if (totalCredits.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    var weightedSum =
        courseGrades.stream()
            .map(cg -> cg.grade().multiply(BigDecimal.valueOf(cg.credits())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return weightedSum.divide(totalCredits, 4, RoundingMode.HALF_UP);
  }

  private BigDecimal weightedAverageOfCourseUnitAverages(
      List<CourseUnitAverage> courseUnitAverages) {
    var totalCredits =
        courseUnitAverages.stream()
            .map(cu -> BigDecimal.valueOf(cu.credits()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    if (totalCredits.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    var weightedSum =
        courseUnitAverages.stream()
            .map(cu -> cu.average().multiply(BigDecimal.valueOf(cu.credits())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return weightedSum.divide(totalCredits, 4, RoundingMode.HALF_UP);
  }

  private BigDecimal weightedExamValue(String studentMatricule, JExam exam) {
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
