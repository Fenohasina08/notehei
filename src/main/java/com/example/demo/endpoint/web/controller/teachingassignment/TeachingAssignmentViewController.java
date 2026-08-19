package com.example.demo.endpoint.web.controller.teachingassignment;

import com.example.demo.model.TeachingAssignment;
import com.example.demo.service.CourseService;
import com.example.demo.service.StudentGroupService;
import com.example.demo.service.TeachingAssignmentService;
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
public class TeachingAssignmentViewController {

  private final TeachingAssignmentService teachingAssignmentService;
  private final CourseService courseService;
  private final StudentGroupService studentGroupService;

  @GetMapping("/screens/teaching-assignments")
  public String list(Model model) {
    model.addAttribute("assignments", teachingAssignmentService.getAll());
    model.addAttribute("courses", courseService.getAllCourses());
    model.addAttribute("groups", studentGroupService.getAll());
    return "teachingassignment/list";
  }

  @PostMapping("/screens/teaching-assignments")
  public String create(
      @RequestParam UUID teacherId, @RequestParam UUID courseId, @RequestParam UUID groupId) {
    teachingAssignmentService.create(new TeachingAssignment(null, teacherId, courseId, groupId));
    return "redirect:/screens/teaching-assignments";
  }
}
