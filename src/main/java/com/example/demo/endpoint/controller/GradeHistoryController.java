package com.example.demo.endpoint.controller;

import com.example.demo.model.GradeHistory;
import com.example.demo.security.JwtService;
import com.example.demo.service.GradeHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grade-history")
@AllArgsConstructor
public class GradeHistoryController {

  private final GradeHistoryService gradeHistoryService;
  private final JwtService jwtService;

  public record ModificationRequest(BigDecimal newValue, String reason) {}

  @PostMapping("/{gradeId}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<GradeHistory> modify(
      @PathVariable UUID gradeId,
      @RequestBody ModificationRequest request,
      HttpServletRequest httpRequest,
      Authentication authentication) {

    var userId = extractUserId(httpRequest);

    var isTeacher =
        authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

    var teacherId = isTeacher ? userId : null;
    var adminId = isTeacher ? null : userId;

    return ResponseEntity.ok(
        gradeHistoryService.recordModification(
            gradeId, request.newValue(), request.reason(), teacherId, adminId));
  }

  @GetMapping("/{gradeId}")
  public ResponseEntity<List<GradeHistory>> history(@PathVariable UUID gradeId) {
    return ResponseEntity.ok(gradeHistoryService.getHistoryForGrade(gradeId));
  }

  private UUID extractUserId(HttpServletRequest request) {
    var header = request.getHeader("Authorization");
    var token = header.substring("Bearer ".length());
    return jwtService.extractUserId(token);
  }
}
