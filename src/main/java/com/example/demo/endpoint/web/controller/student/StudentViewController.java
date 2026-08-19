package com.example.demo.endpoint.web.controller.student;

import com.example.demo.service.GroupMembershipService;
import com.example.demo.service.StudentService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class StudentViewController {

  private final StudentService studentService;
  private final GroupMembershipService groupMembershipService;

  @GetMapping("/screens/students/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN','TEACHER') or (hasRole('STUDENT') and"
          + " #id.equals(authentication.principal.id))")
  public String detail(@PathVariable UUID id, Model model) {
    model.addAttribute("student", studentService.findById(id));
    model.addAttribute("membershipHistory", groupMembershipService.getHistoryForStudent(id));
    model.addAttribute("activeMembership", groupMembershipService.getActiveMembership(id));
    return "student/detail";
  }
}
