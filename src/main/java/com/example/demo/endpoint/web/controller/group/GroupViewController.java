package com.example.demo.endpoint.web.controller.group;

import com.example.demo.model.StudentGroup;
import com.example.demo.service.CohortService;
import com.example.demo.service.GroupProgramHistoryService;
import com.example.demo.service.ProgramService;
import com.example.demo.service.StudentGroupService;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class GroupViewController {

  private final StudentGroupService studentGroupService;
  private final CohortService cohortService;
  private final ProgramService programService;
  private final GroupProgramHistoryService groupProgramHistoryService;

  @GetMapping("/screens/groups")
  public String list(Model model) {
    model.addAttribute("groups", studentGroupService.getAll());
    model.addAttribute("cohorts", cohortService.getAll());
    return "group/list";
  }

  @PostMapping("/screens/groups")
  public String create(@RequestParam String reference, @RequestParam UUID cohortId) {
    studentGroupService.create(new StudentGroup(null, reference, cohortId));
    return "redirect:/screens/groups";
  }

  @GetMapping("/screens/groups/{id}")
  public String detail(@PathVariable UUID id, Model model) {
    model.addAttribute("group", studentGroupService.getById(id));
    model.addAttribute("history", groupProgramHistoryService.getHistoryForGroup(id));
    model.addAttribute("programs", programService.getAll());
    return "group/detail";
  }

  @PostMapping("/screens/groups/{id}/program-history")
  public String assignProgram(@PathVariable UUID id, @RequestParam UUID programId) {
    groupProgramHistoryService.assignProgram(id, programId, LocalDate.now());
    return "redirect:/screens/groups/" + id;
  }
}
