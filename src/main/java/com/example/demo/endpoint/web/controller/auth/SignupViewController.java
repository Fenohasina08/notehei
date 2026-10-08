package com.example.demo.endpoint.web.controller.auth;

import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.exception.EmailAlreadyUsedException;
import com.example.demo.service.StudentService;
import com.example.demo.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignupViewController {

  private final StudentService studentService;
  private final TeacherService teacherService;

  @InitBinder("studentDto")
  public void debugStudentBinder(WebDataBinder binder) {

    System.out.println("============================================================");
    System.out.println("===== WEB DATA BINDER DEBUG - STUDENT =====");
    System.out.println("============================================================");

    System.out.println(
        "target class = "
            + (binder.getTarget() != null ? binder.getTarget().getClass().getName() : "null"));

    System.out.println("allowed fields = " + Arrays.toString(binder.getAllowedFields()));

    System.out.println("disallowed fields = " + Arrays.toString(binder.getDisallowedFields()));

    System.out.println("ignore unknown fields = " + binder.isIgnoreUnknownFields());

    System.out.println("ignore invalid fields = " + binder.isIgnoreInvalidFields());

    System.out.println("auto grow nested paths = " + binder.isAutoGrowNestedPaths());

    System.out.println("============================================================");
  }

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
      Model model,
      HttpServletRequest request) {

    System.out.println("============================================================");
    System.out.println("===== REQUEST DIRECT DEBUG =====");
    System.out.println("============================================================");

    System.out.println("request.getParameter(firstName) = " + request.getParameter("firstName"));

    System.out.println("request.getParameter(lastName) = " + request.getParameter("lastName"));

    System.out.println("request.getParameter(email) = " + request.getParameter("email"));

    System.out.println("request.getParameter(password) = " + request.getParameter("password"));

    System.out.println("request.getParameter(birthdate) = " + request.getParameter("birthdate"));

    System.out.println("request.getParameter(address) = " + request.getParameter("address"));

    System.out.println("request.getParameterMap = " + request.getParameterMap());

    System.out.println("============================================================");
    System.out.println("===== BINDING DEBUG - STUDENT =====");
    System.out.println("============================================================");

    System.out.println("DTO class = " + studentDto.getClass().getName());

    System.out.println(
        "setFirstName exists = "
            + Arrays.stream(studentDto.getClass().getMethods())
                .anyMatch(method -> method.getName().equals("setFirstName")));

    System.out.println(
        "setLastName exists = "
            + Arrays.stream(studentDto.getClass().getMethods())
                .anyMatch(method -> method.getName().equals("setLastName")));

    System.out.println(
        "setEmail exists = "
            + Arrays.stream(studentDto.getClass().getMethods())
                .anyMatch(method -> method.getName().equals("setEmail")));

    System.out.println(
        "setPassword exists = "
            + Arrays.stream(studentDto.getClass().getMethods())
                .anyMatch(method -> method.getName().equals("setPassword")));

    System.out.println(
        "setBirthdate exists = "
            + Arrays.stream(studentDto.getClass().getMethods())
                .anyMatch(method -> method.getName().equals("setBirthdate")));

    System.out.println(
        "setAddress exists = "
            + Arrays.stream(studentDto.getClass().getMethods())
                .anyMatch(method -> method.getName().equals("setAddress")));

    System.out.println(
        "suppressed fields = " + Arrays.toString(bindingResult.getSuppressedFields()));

    System.out.println("============================================================");
    System.out.println("===== STUDENT DTO =====");
    System.out.println("============================================================");

    System.out.println("firstName = [" + studentDto.getFirstName() + "]");

    System.out.println("lastName = [" + studentDto.getLastName() + "]");

    System.out.println("email = [" + studentDto.getEmail() + "]");

    System.out.println("password = [" + studentDto.getPassword() + "]");

    System.out.println("birthdate = [" + studentDto.getBirthdate() + "]");

    System.out.println("address = [" + studentDto.getAddress() + "]");

    System.out.println("hasErrors = " + bindingResult.hasErrors());

    if (bindingResult.hasErrors()) {

      System.out.println("============================================================");
      System.out.println("===== VALIDATION ERRORS =====");
      System.out.println("============================================================");

      bindingResult
          .getFieldErrors()
          .forEach(
              error ->
                  System.out.println(
                      "ERROR field="
                          + error.getField()
                          + " message="
                          + error.getDefaultMessage()
                          + " rejectedValue=["
                          + error.getRejectedValue()
                          + "]"));
    }

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

    System.out.println("============================================================");
    System.out.println("===== BINDING DEBUG - TEACHER =====");
    System.out.println("============================================================");

    System.out.println("DTO class = " + teacherDto.getClass().getName());

    System.out.println(
        "suppressed fields = " + Arrays.toString(bindingResult.getSuppressedFields()));

    model.addAttribute("studentDto", CreateStudentDTO.builder().build());

    model.addAttribute("activeTab", "teacher");

    if (bindingResult.hasErrors()) {

      bindingResult
          .getFieldErrors()
          .forEach(
              error ->
                  System.out.println(
                      "ERROR field="
                          + error.getField()
                          + " message="
                          + error.getDefaultMessage()
                          + " rejectedValue=["
                          + error.getRejectedValue()
                          + "]"));

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
