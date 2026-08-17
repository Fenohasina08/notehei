package com.example.demo.endpoint.controller;

import com.example.demo.model.Transcript;
import com.example.demo.security.JwtService;
import com.example.demo.service.TranscriptService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transcripts")
@AllArgsConstructor
public class TranscriptController {

  private final TranscriptService transcriptService;
  private final JwtService jwtService;

  @PostMapping("/student/{studentId}/semester/{semesterId}")
  @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
  public ResponseEntity<Transcript> request(
      @PathVariable UUID studentId,
      @PathVariable UUID semesterId,
      HttpServletRequest httpRequest,
      Authentication authentication) {
    var requesterId = extractUserId(httpRequest);
    var requesterIsAdmin =
        authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    var transcript =
        transcriptService.requestTranscript(studentId, semesterId, requesterId, requesterIsAdmin);
    return new ResponseEntity<>(transcript, HttpStatus.CREATED);
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<Transcript>> listForStudent(@PathVariable UUID studentId) {
    return ResponseEntity.ok(transcriptService.getTranscriptsForStudent(studentId));
  }

  private UUID extractUserId(HttpServletRequest request) {
    var header = request.getHeader("Authorization");
    var token = header.substring("Bearer ".length());
    return jwtService.extractUserId(token);
  }
}
