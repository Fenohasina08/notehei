package com.example.demo.endpoint.controller;

import com.example.demo.model.Cohort;
import com.example.demo.service.CohortService;
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
@RequestMapping("/cohorts")
@AllArgsConstructor
public class CohortController {

  private final CohortService cohortService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Cohort> create(@RequestBody Cohort cohort) {
    Cohort created = cohortService.create(cohort);
    return ResponseEntity.created(URI.create("/cohorts/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<Cohort>> getAll() {
    return ResponseEntity.ok(cohortService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<Cohort> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(cohortService.getById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    cohortService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
