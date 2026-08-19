package com.example.demo.endpoint.web.controller.grade;

import com.example.demo.model.Grade;
import com.example.demo.security.Role;
import com.example.demo.security.SecurityUser;
import com.example.demo.service.ExamService;
import com.example.demo.service.GradeService;
import com.example.demo.service.StudentService;
import com.example.demo.service.TeachingAssignmentService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class GradeViewController {

  private final GradeService gradeService;
  private final ExamService examService;
  private final TeachingAssignmentService teachingAssignmentService;
  private final StudentService studentService;

  /** Teacher's grade entry form: pick one of their courses, then one of its exams. */
  @GetMapping("/screens/grades")
  @PreAuthorize("hasRole('TEACHER')")
  public String form(
      @RequestParam(required = false) UUID courseId,
      @AuthenticationPrincipal SecurityUser user,
      Model model) {
    var assignments = teachingAssignmentService.getForTeacher(user.getId());
    model.addAttribute("assignments", assignments);
    model.addAttribute("selectedCourseId", courseId);
    if (courseId != null) {
      model.addAttribute("exams", examService.getExamsForCourse(courseId));
    }
    return "grade/form";
  }

  @PostMapping("/screens/grades")
  @PreAuthorize("hasRole('TEACHER')")
  public String create(
      @RequestParam String studentMatricule,
      @RequestParam UUID examId,
      @RequestParam BigDecimal value,
      @AuthenticationPrincipal SecurityUser user) {
    var grade =
        new Grade(
            null, studentMatricule, examId, value, "SUBMITTED", LocalDateTime.now(), null, null);
    gradeService.createGradeByTeacher(grade, user.getId());
    return "redirect:/screens/grades/list?studentMatricule=" + studentMatricule;
  }

  /** Consultation: a student's grades (self-service for students, lookup for staff). */
  @GetMapping("/screens/grades/list")
  public String list(
      @RequestParam(required = false) String studentMatricule,
      @AuthenticationPrincipal SecurityUser user,
      Model model) {
    String matricule = studentMatricule;
    if (user.getRole() == Role.STUDENT) {
      matricule = studentService.findById(user.getId()).getMatricule();
    }
    model.addAttribute("studentMatricule", matricule);
    if (matricule != null) {
      model.addAttribute("grades", gradeService.getGradesForStudent(matricule));
    }
    return "grade/list";
  }

  /** Dashboard: students missing a grade for one of the teacher's exams. */
  @GetMapping("/screens/grades/dashboard")
  @PreAuthorize("hasRole('TEACHER')")
  public String dashboard(
      @RequestParam(required = false) UUID courseId,
      @RequestParam(required = false) UUID examId,
      @AuthenticationPrincipal SecurityUser user,
      Model model) {
    var assignments = teachingAssignmentService.getForTeacher(user.getId());
    model.addAttribute("assignments", assignments);
    model.addAttribute("selectedCourseId", courseId);
    model.addAttribute("selectedExamId", examId);

    if (courseId != null) {
      model.addAttribute("exams", examService.getExamsForCourse(courseId));
    }
    if (examId != null) {
      model.addAttribute(
          "missingGrades", gradeService.getStudentsMissingGradeForExam(user.getId(), examId));
    }
    return "grade/dashboard";
  }
}
