package com.example.demo.endpoint.controller;

import com.example.demo.model.Exam;
import com.example.demo.service.ExamService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exams")
@AllArgsConstructor
public class ExamController {

  private final ExamService examService;

  @PostMapping
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Exam> create(@RequestBody Exam exam) {
    return new ResponseEntity<>(examService.createExam(exam), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Exam>> listByCourse(@RequestParam UUID courseId) {
    return ResponseEntity.ok(examService.getExamsForCourse(courseId));
  }

  @GetMapping("/course/{courseId}/weighting-complete")
  public ResponseEntity<Boolean> isWeightingComplete(@PathVariable UUID courseId) {
    return ResponseEntity.ok(examService.isCourseWeightingComplete(courseId));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    examService.deleteExam(id);
    return ResponseEntity.noContent().build();
  }
}
