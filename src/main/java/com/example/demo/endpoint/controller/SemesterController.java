package com.example.demo.endpoint.controller;

import com.example.demo.model.Semester;
import com.example.demo.service.SemesterService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/semesters")
@AllArgsConstructor
public class SemesterController {

  private final SemesterService semesterService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Semester> create(@RequestBody Semester semester) {
    Semester created = semesterService.create(semester);
    return ResponseEntity.created(URI.create("/semesters/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<Semester>> getAll() {
    return ResponseEntity.ok(semesterService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<Semester> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(semesterService.getById(id));
  }

  @GetMapping("/{id}/credit-structure-complete")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<Boolean> isCreditStructureComplete(@PathVariable UUID id) {
    return ResponseEntity.ok(semesterService.isCreditStructureComplete(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    semesterService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
