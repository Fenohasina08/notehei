package com.example.demo.endpoint.web.controller.courseunit;

import com.example.demo.model.CourseUnit;
import com.example.demo.service.CourseUnitService;
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
public class CourseUnitViewController {

  private final CourseUnitService courseUnitService;
  private final SemesterService semesterService;

  @GetMapping("/screens/course-units")
  public String list(@RequestParam(required = false) UUID semesterId, Model model) {
    model.addAttribute("semesters", semesterService.getAll());
    model.addAttribute("selectedSemesterId", semesterId);
    if (semesterId != null) {
      model.addAttribute("courseUnits", courseUnitService.getBySemester(semesterId));
    }
    return "courseunit/list";
  }

  @PostMapping("/screens/course-units")
  public String create(
      @RequestParam String code,
      @RequestParam String name,
      @RequestParam Integer credits,
      @RequestParam UUID semesterId) {
    courseUnitService.create(new CourseUnit(null, code, name, credits, semesterId));
    return "redirect:/screens/course-units?semesterId=" + semesterId;
  }
}
