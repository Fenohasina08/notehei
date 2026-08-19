package com.example.demo.endpoint.web.controller.semester;

import com.example.demo.model.Semester;
import com.example.demo.service.AcademicYearService;
import com.example.demo.service.CohortService;
import com.example.demo.service.SemesterService;
import java.util.UUID;
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
public class SemesterViewController {

  private final SemesterService semesterService;
  private final CohortService cohortService;
  private final AcademicYearService academicYearService;

  @GetMapping("/screens/semesters")
  public String list(Model model) {
    model.addAttribute("semesters", semesterService.getAll());
    model.addAttribute("cohorts", cohortService.getAll());
    model.addAttribute("academicYears", academicYearService.getAll());
    return "semester/list";
  }

  @PostMapping("/screens/semesters")
  public String create(
      @RequestParam Integer number,
      @RequestParam UUID cohortId,
      @RequestParam UUID academicYearId) {
    semesterService.create(new Semester(null, number, cohortId, academicYearId));
    return "redirect:/screens/semesters";
  }
}
