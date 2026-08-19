package com.example.demo.endpoint.web.controller.course;

import com.example.demo.model.Course;
import com.example.demo.service.CourseService;
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
public class CourseViewController {

  private final CourseService courseService;

  @GetMapping("/screens/courses")
  public String list(Model model) {
    model.addAttribute("courses", courseService.getAllCourses());
    return "course/list";
  }

  @PostMapping("/screens/courses")
  public String create(
      @RequestParam String reference,
      @RequestParam String title,
      @RequestParam java.math.BigDecimal coefficient) {
    courseService.createCourse(new Course(null, reference, title, coefficient));
    return "redirect:/screens/courses";
  }
}
