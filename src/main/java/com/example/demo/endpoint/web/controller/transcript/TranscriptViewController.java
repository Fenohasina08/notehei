package com.example.demo.endpoint.web.controller.transcript;

import com.example.demo.security.Role;
import com.example.demo.security.SecurityUser;
import com.example.demo.service.SemesterService;
import com.example.demo.service.TranscriptService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class TranscriptViewController {

  private final TranscriptService transcriptService;
  private final SemesterService semesterService;

  @GetMapping("/screens/transcripts")
  public String list(
      @RequestParam(required = false) UUID studentId,
      @AuthenticationPrincipal SecurityUser user,
      Model model) {
    UUID targetStudentId = user.getRole() == Role.STUDENT ? user.getId() : studentId;

    model.addAttribute("studentId", targetStudentId);
    model.addAttribute("semesters", semesterService.getAll());
    if (targetStudentId != null) {
      model.addAttribute(
          "transcripts", transcriptService.getTranscriptsForStudent(targetStudentId));
    }
    return "transcript/detail";
  }

  /**
   * Requests generation and, once ready, the email delivery of a transcript for {@code studentId}
   * (a student always requests their own; an admin must supply {@code studentId}). This single
   * action covers both the "generate PDF" and "send by email" buttons from the spec: the async
   * worker ({@code TranscriptRequestedEventService}) generates the PDF, uploads it, then
   * automatically fires the email — there is no separate manual "send" step once generation
   * succeeds.
   */
  @PostMapping("/screens/transcripts")
  public String requestTranscript(
      @RequestParam UUID semesterId,
      @RequestParam(required = false) UUID studentId,
      @AuthenticationPrincipal SecurityUser user) {
    boolean isAdmin = user.getRole() == Role.ADMIN;
    UUID targetStudentId = isAdmin ? studentId : user.getId();
    transcriptService.requestTranscript(targetStudentId, semesterId, user.getId(), isAdmin);
    return "redirect:/screens/transcripts?studentId=" + targetStudentId;
  }
}
