package com.example.demo.endpoint.controller;

import com.example.demo.model.Program;
import com.example.demo.service.ProgramService;
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
@RequestMapping("/programs")
@AllArgsConstructor
public class ProgramController {

  private final ProgramService programService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Program> create(@RequestBody Program program) {
    Program created = programService.create(program);
    return ResponseEntity.created(URI.create("/programs/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<Program>> getAll() {
    return ResponseEntity.ok(programService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<Program> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(programService.getById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    programService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
