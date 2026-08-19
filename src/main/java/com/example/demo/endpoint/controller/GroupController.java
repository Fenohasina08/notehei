package com.example.demo.endpoint.controller;

import com.example.demo.model.GroupProgramHistory;
import com.example.demo.model.StudentGroup;
import com.example.demo.service.GroupProgramHistoryService;
import com.example.demo.service.StudentGroupService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/groups")
@AllArgsConstructor
public class GroupController {

  private final StudentGroupService studentGroupService;
  private final GroupProgramHistoryService groupProgramHistoryService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StudentGroup> create(@RequestBody StudentGroup group) {
    StudentGroup created = studentGroupService.create(group);
    return ResponseEntity.created(URI.create("/groups/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<StudentGroup>> getAll(@RequestParam(required = false) UUID cohortId) {
    if (cohortId != null) {
      return ResponseEntity.ok(studentGroupService.getByCohort(cohortId));
    }
    return ResponseEntity.ok(studentGroupService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<StudentGroup> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(studentGroupService.getById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    studentGroupService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // --- Program history ----------------------------------------------------------------------

  @PostMapping("/{id}/program-history")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GroupProgramHistory> assignProgram(
      @PathVariable UUID id, @RequestBody Map<String, Object> body) {
    UUID programId = UUID.fromString((String) body.get("programId"));
    LocalDate startDate = LocalDate.parse((String) body.get("startDate"));
    return new ResponseEntity<>(
        groupProgramHistoryService.assignProgram(id, programId, startDate), HttpStatus.CREATED);
  }

  @GetMapping("/{id}/program-history")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<GroupProgramHistory>> getProgramHistory(@PathVariable UUID id) {
    return ResponseEntity.ok(groupProgramHistoryService.getHistoryForGroup(id));
  }

  @GetMapping("/{id}/program-history/active")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<GroupProgramHistory> getActiveProgram(@PathVariable UUID id) {
    return ResponseEntity.ok(groupProgramHistoryService.getActiveProgram(id));
  }
}
