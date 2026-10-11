package com.example.demo.endpoint.web.controller.auth;

import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.exception.EmailAlreadyUsedException;
import com.example.demo.service.StudentService;
import com.example.demo.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignupViewController {

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final Validator validator;

  @GetMapping("/register")
  public String register(Model model) {
    if (!model.containsAttribute("studentDto")) {
      model.addAttribute("studentDto", new CreateStudentDTO());
    }
    if (!model.containsAttribute("teacherDto")) {
      model.addAttribute("teacherDto", new CreateTeacherDTO());
    }
    return "auth/register";
  }

  @PostMapping("/register/student")
  public String registerStudent(HttpServletRequest request, Model model) {
    CreateStudentDTO dto = new CreateStudentDTO();
    dto.setFirstName(param(request, "firstName"));
    dto.setLastName(param(request, "lastName"));
    dto.setEmail(param(request, "email"));
    dto.setPassword(request.getParameter("password")); // pas de trim sur le mot de passe
    dto.setAddress(param(request, "address"));

    BindingResult result = validate(dto, "studentDto");
    parseBirthdate(request.getParameter("birthdate"), result, dto::setBirthdate);

    model.addAttribute("studentDto", dto);
    model.addAttribute("teacherDto", new CreateTeacherDTO());
    model.addAttribute("activeTab", "student");
    model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "studentDto", result);

    if (result.hasErrors()) {
      return "auth/register";
    }

    try {
      var created = studentService.create(dto);
      model.addAttribute("successRole", "Student");
      model.addAttribute("successMatricule", created.getMatricule());
      return "auth/register-success";
    } catch (EmailAlreadyUsedException e) {
      model.addAttribute("emailError", "Cet email est déjà utilisé.");
      return "auth/register";
    }
  }

  @PostMapping("/register/teacher")
  public String registerTeacher(HttpServletRequest request, Model model) {
    CreateTeacherDTO dto = new CreateTeacherDTO();
    dto.setFirstName(param(request, "firstName"));
    dto.setLastName(param(request, "lastName"));
    dto.setEmail(param(request, "email"));
    dto.setPassword(request.getParameter("password"));
    dto.setAddress(param(request, "address"));

    BindingResult result = validate(dto, "teacherDto");
    parseBirthdate(request.getParameter("birthdate"), result, dto::setBirthdate);

    model.addAttribute("teacherDto", dto);
    model.addAttribute("studentDto", new CreateStudentDTO());
    model.addAttribute("activeTab", "teacher");
    model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "teacherDto", result);

    if (result.hasErrors()) {
      return "auth/register";
    }

    try {
      var created = teacherService.create(dto);
      model.addAttribute("successRole", "Teacher");
      model.addAttribute("successMatricule", created.getMatricule());
      return "auth/register-success";
    } catch (EmailAlreadyUsedException e) {
      model.addAttribute("emailError", "Cet email est déjà utilisé.");
      return "auth/register";
    }
  }

  /** Valide le DTO avec Bean Validation et retourne un BindingResult exploitable par Thymeleaf. */
  private BindingResult validate(Object dto, String objectName) {
    BindingResult result = new BeanPropertyBindingResult(dto, objectName);
    new SpringValidatorAdapter(validator).validate(dto, result);
    return result;
  }

  /** Parse la date de naissance (format ISO yyyy-MM-dd envoyé par input type="date"). */
  private void parseBirthdate(
      String raw, BindingResult result, java.util.function.Consumer<LocalDate> setter) {
    if (raw == null || raw.isBlank()) {
      return;
    }
    try {
      setter.accept(LocalDate.parse(raw.trim()));
    } catch (DateTimeParseException e) {
      result.rejectValue("birthdate", "typeMismatch", "La date de naissance est invalide.");
    }
  }

  /** Retourne le paramètre sans espaces autour, ou null s'il est absent. */
  private String param(HttpServletRequest request, String name) {
    String value = request.getParameter(name);
    return value == null ? null : value.trim();
  }
}
