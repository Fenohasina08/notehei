package com.example.demo.endpoint.controller;

import com.example.demo.model.CourseUnit;
import com.example.demo.service.CourseUnitService;
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
@RequestMapping("/course-units")
@AllArgsConstructor
public class CourseUnitController {

  private final CourseUnitService courseUnitService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CourseUnit> create(@RequestBody CourseUnit courseUnit) {
    CourseUnit created = courseUnitService.create(courseUnit);
    return ResponseEntity.created(URI.create("/course-units/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<CourseUnit>> getBySemester(@RequestParam UUID semesterId) {
    return ResponseEntity.ok(courseUnitService.getBySemester(semesterId));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<CourseUnit> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(courseUnitService.getById(id));
  }

  @PostMapping("/{id}/programs/{programId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> attachProgram(@PathVariable UUID id, @PathVariable UUID programId) {
    courseUnitService.attachProgram(id, programId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{id}/programs/{programId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> detachProgram(@PathVariable UUID id, @PathVariable UUID programId) {
    courseUnitService.detachProgram(id, programId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{id}/complete")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<Void> validateComplete(@PathVariable UUID id) {
    courseUnitService.validateComplete(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    courseUnitService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
