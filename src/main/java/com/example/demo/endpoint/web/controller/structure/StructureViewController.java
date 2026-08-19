package com.example.demo.endpoint.web.controller.structure;

import com.example.demo.model.Cohort;
import com.example.demo.model.GroupProgramHistory;
import com.example.demo.model.StudentGroup;
import com.example.demo.service.CohortService;
import com.example.demo.service.GroupProgramHistoryService;
import com.example.demo.service.StudentGroupService;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class StructureViewController {

  private final CohortService cohortService;
  private final StudentGroupService studentGroupService;
  private final GroupProgramHistoryService groupProgramHistoryService;

  @GetMapping("/screens/structure/organigramme")
  public String organigramme(Model model) {
    Map<Cohort, java.util.List<StudentGroup>> groupsByCohort = new LinkedHashMap<>();
    Map<StudentGroup, GroupProgramHistory> activeProgramByGroup = new LinkedHashMap<>();

    for (Cohort cohort : cohortService.getAll()) {
      var groups = studentGroupService.getByCohort(cohort.id());
      groupsByCohort.put(cohort, groups);
      for (StudentGroup group : groups) {
        activeProgramByGroup.put(group, groupProgramHistoryService.getActiveProgram(group.id()));
      }
    }

    model.addAttribute("groupsByCohort", groupsByCohort);
    model.addAttribute("activeProgramByGroup", activeProgramByGroup);
    return "structure/organigramme";
  }
}
