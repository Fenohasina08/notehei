package com.example.demo.endpoint.controller;

import com.example.demo.model.Grade;
import com.example.demo.security.JwtService;
import com.example.demo.service.GradeService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grades")
@AllArgsConstructor
public class GradeController {

  private final GradeService gradeService;
  private final JwtService jwtService;

  @PostMapping
  @PreAuthorize("hasRole('TEACHER')")
  public ResponseEntity<Grade> createByTeacher(
      @RequestBody Grade grade, HttpServletRequest request) {
    var teacherId = extractUserId(request);

    return new ResponseEntity<>(
        gradeService.createGradeByTeacher(grade, teacherId), HttpStatus.CREATED);
  }

  @PostMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Grade> createByAdmin(@RequestBody Grade grade, HttpServletRequest request) {
    var adminId = extractUserId(request);

    return new ResponseEntity<>(
        gradeService.createGradeByAdmin(grade, adminId), HttpStatus.CREATED);
  }

  @GetMapping("/student/{studentMatricule}")
  public ResponseEntity<List<Grade>> listForStudent(@PathVariable String studentMatricule) {
    return ResponseEntity.ok(gradeService.getGradesForStudent(studentMatricule));
  }

  @GetMapping("/student/{studentMatricule}/course/{courseId}/average")
  public ResponseEntity<BigDecimal> retainedGrade(
      @PathVariable String studentMatricule, @PathVariable UUID courseId) {
    return ResponseEntity.ok(gradeService.computeRetainedGrade(studentMatricule, courseId));
  }

  @GetMapping("/exam/{examId}/missing")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<List<com.example.demo.model.MissingGrade>> missingGradesForExam(
      @PathVariable UUID examId, HttpServletRequest request) {
    var teacherId = extractUserId(request);
    return ResponseEntity.ok(gradeService.getStudentsMissingGradeForExam(teacherId, examId));
  }

  private UUID extractUserId(HttpServletRequest request) {
    var header = request.getHeader("Authorization");
    var token = header.substring("Bearer ".length());
    return jwtService.extractUserId(token);
  }
}
