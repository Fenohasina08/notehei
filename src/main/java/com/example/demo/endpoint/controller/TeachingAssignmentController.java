package com.example.demo.endpoint.controller;

import com.example.demo.model.TeachingAssignment;
import com.example.demo.service.TeachingAssignmentService;
import java.net.URI;
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
@RequestMapping("/teaching-assignments")
@AllArgsConstructor
public class TeachingAssignmentController {

  private final TeachingAssignmentService teachingAssignmentService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<TeachingAssignment> create(@RequestBody TeachingAssignment assignment) {
    TeachingAssignment created = teachingAssignmentService.create(assignment);
    return ResponseEntity.created(URI.create("/teaching-assignments/" + created.id()))
        .body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<List<TeachingAssignment>> getAll(
      @RequestParam(required = false) UUID teacherId) {
    if (teacherId != null) {
      return ResponseEntity.ok(teachingAssignmentService.getForTeacher(teacherId));
    }
    return ResponseEntity.ok(teachingAssignmentService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<TeachingAssignment> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(teachingAssignmentService.getById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    teachingAssignmentService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
