package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordDTO;
import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.service.TeacherService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

  private final TeacherService teacherService;

  @PostMapping
  public ResponseEntity<TeacherResponseDTO> create(@Valid @RequestBody CreateTeacherDTO dto) {
    TeacherResponseDTO created = teacherService.create(dto);
    return ResponseEntity.created(URI.create("/teachers/" + created.getId())).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TeacherResponseDTO> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(teacherService.findById(id));
  }

  @GetMapping(params = "email")
  public ResponseEntity<TeacherResponseDTO> findByEmail(@RequestParam String email) {
    return ResponseEntity.ok(teacherService.findByEmail(email));
  }

  @PreAuthorize(
      "hasRole('ADMIN') or " + "(hasRole('TEACHER') and #id.equals(authentication.principal.id()))")
  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable UUID id, @Valid @RequestBody ChangePasswordDTO dto) {
    teacherService.changePassword(id, dto);
    return ResponseEntity.noContent().build();
  }
}
