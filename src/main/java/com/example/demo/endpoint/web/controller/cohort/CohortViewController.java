package com.example.demo.endpoint.web.controller.cohort;

import com.example.demo.model.Cohort;
import com.example.demo.service.CohortService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class CohortViewController {

  private final CohortService cohortService;

  @GetMapping("/screens/cohorts")
  public String list(Model model) {
    model.addAttribute("cohorts", cohortService.getAll());
    return "cohort/list";
  }

  @PostMapping("/screens/cohorts")
  public String create(@RequestParam Integer entryYear) {
    cohortService.create(new Cohort(null, entryYear));
    return "redirect:/screens/cohorts";
  }
}
