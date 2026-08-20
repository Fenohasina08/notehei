package com.example.demo.endpoint.web.controller.auth;

import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.exception.EmailAlreadyUsedException;
import com.example.demo.service.StudentService;
import com.example.demo.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignupViewController {

  private final StudentService studentService;
  private final TeacherService teacherService;

  @GetMapping("/register")
  public String register(Model model) {
    if (!model.containsAttribute("studentDto")) {
      model.addAttribute("studentDto", CreateStudentDTO.builder().build());
    }
    if (!model.containsAttribute("teacherDto")) {
      model.addAttribute("teacherDto", CreateTeacherDTO.builder().build());
    }
    return "auth/register";
  }

  @PostMapping("/register/student")
  public String registerStudent(
      @Valid @ModelAttribute("studentDto") CreateStudentDTO studentDto,
      BindingResult bindingResult,
      Model model) {

    model.addAttribute("teacherDto", CreateTeacherDTO.builder().build());
    model.addAttribute("activeTab", "student");

    if (bindingResult.hasErrors()) {
      return "auth/register";
    }

    try {
      var created = studentService.create(studentDto);
      model.addAttribute("successRole", "Student");
      model.addAttribute("successMatricule", created.getMatricule());
      return "auth/register-success";
    } catch (EmailAlreadyUsedException e) {
      model.addAttribute("emailError", "Cet email est déjà utilisé.");
      return "auth/register";
    }
  }

  @PostMapping("/register/teacher")
  public String registerTeacher(
      @Valid @ModelAttribute("teacherDto") CreateTeacherDTO teacherDto,
      BindingResult bindingResult,
      Model model) {

    model.addAttribute("studentDto", CreateStudentDTO.builder().build());
    model.addAttribute("activeTab", "teacher");

    if (bindingResult.hasErrors()) {
      return "auth/register";
    }

    try {
      var created = teacherService.create(teacherDto);
      model.addAttribute("successRole", "Teacher");
      model.addAttribute("successMatricule", created.getMatricule());
      return "auth/register-success";
    } catch (EmailAlreadyUsedException e) {
      model.addAttribute("emailError", "Cet email est déjà utilisé.");
      return "auth/register";
    }
  }
}
