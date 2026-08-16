package com.example.demo.endpoint.controller;

import com.example.demo.model.Grade;
import com.example.demo.service.GradeService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

  @PostMapping
  @PreAuthorize("hasRole('TEACHER')")
  public ResponseEntity<Grade> createByTeacher(
      @RequestBody Grade grade, Authentication authentication) {
    var teacherId = UUID.fromString(authentication.getName());
    return new ResponseEntity<>(
        gradeService.createGradeByTeacher(grade, teacherId), HttpStatus.CREATED);
  }

  @PostMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Grade> createByAdmin(
      @RequestBody Grade grade, Authentication authentication) {
    var adminId = UUID.fromString(authentication.getName());
    return new ResponseEntity<>(
        gradeService.createGradeByAdmin(grade, adminId), HttpStatus.CREATED);
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<Grade>> listForStudent(@PathVariable UUID studentId) {
    return ResponseEntity.ok(gradeService.getGradesForStudent(studentId));
  }

  @GetMapping("/student/{studentId}/course/{courseId}/average")
  public ResponseEntity<BigDecimal> retainedGrade(
      @PathVariable UUID studentId, @PathVariable UUID courseId) {
    return ResponseEntity.ok(gradeService.computeRetainedGrade(studentId, courseId));
  }
}
