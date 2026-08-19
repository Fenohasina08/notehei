package com.example.demo.endpoint.controller;

import com.example.demo.model.AcademicYear;
import com.example.demo.service.AcademicYearService;
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
@RequestMapping("/academic-years")
@AllArgsConstructor
public class AcademicYearController {

  private final AcademicYearService academicYearService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AcademicYear> create(@RequestBody AcademicYear academicYear) {
    AcademicYear created = academicYearService.create(academicYear);
    return ResponseEntity.created(URI.create("/academic-years/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<AcademicYear>> getAll() {
    return ResponseEntity.ok(academicYearService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<AcademicYear> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(academicYearService.getById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    academicYearService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
