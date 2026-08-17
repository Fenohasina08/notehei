package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordDTO;
import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.service.StudentService;
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
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;

  @PostMapping
  public ResponseEntity<StudentResponseDTO> create(@Valid @RequestBody CreateStudentDTO dto) {
    StudentResponseDTO created = studentService.create(dto);
    return ResponseEntity.created(URI.create("/students/" + created.getId())).body(created);
  }

  @PreAuthorize(
      "hasAnyRole('ADMIN','TEACHER') or "
          + "(hasRole('STUDENT') and #id.equals(authentication.principal.id()))")
  @GetMapping("/{id}")
  public ResponseEntity<StudentResponseDTO> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(studentService.findById(id));
  }

  @PreAuthorize(
      "hasAnyRole('ADMIN','TEACHER') or "
          + "(hasRole('STUDENT') and #email.equalsIgnoreCase(authentication.principal.email()))")
  @GetMapping(params = "email")
  public ResponseEntity<StudentResponseDTO> findByEmail(@RequestParam String email) {
    return ResponseEntity.ok(studentService.findByEmail(email));
  }

  @PreAuthorize(
      "hasRole('ADMIN') or " + "(hasRole('STUDENT') and #id.equals(authentication.principal.id()))")
  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable UUID id, @Valid @RequestBody ChangePasswordDTO dto) {
    studentService.changePassword(id, dto);
    return ResponseEntity.noContent().build();
  }
}
