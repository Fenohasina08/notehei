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

    /*
     * ============================================================
     * DEBUG - Vérification du binding du formulaire étudiant
     * ============================================================
     *
     * Ces logs permettent de vérifier si les valeurs envoyées
     * par register.html arrivent correctement dans CreateStudentDTO.
     */
    System.out.println("===== STUDENT DTO =====");
    System.out.println("firstName = [" + studentDto.getFirstName() + "]");
    System.out.println("lastName = [" + studentDto.getLastName() + "]");
    System.out.println("email = [" + studentDto.getEmail() + "]");
    System.out.println("password = [" + studentDto.getPassword() + "]");
    System.out.println("birthdate = [" + studentDto.getBirthdate() + "]");
    System.out.println("address = [" + studentDto.getAddress() + "]");
    System.out.println("hasErrors = " + bindingResult.hasErrors());

    /*
     * Affiche toutes les erreurs de validation éventuelles.
     */
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

    /*
     * On prépare le DTO enseignant car la page register.html
     * contient également le formulaire enseignant.
     */
    model.addAttribute("teacherDto", CreateTeacherDTO.builder().build());

    /*
     * On indique à la page que l'onglet étudiant doit rester actif
     * lorsque le formulaire contient une erreur.
     */
    model.addAttribute("activeTab", "student");

    /*
     * Si la validation échoue, on retourne simplement sur
     * la page d'inscription avec les erreurs.
     */
    if (bindingResult.hasErrors()) {
      return "auth/register";
    }

    /*
     * Si toutes les données sont valides, on crée le compte étudiant.
     */
    try {

      var created = studentService.create(studentDto);

      /*
       * Informations utilisées par register-success.html
       * pour afficher le résultat de l'inscription.
       */
      model.addAttribute("successRole", "Student");

      model.addAttribute("successMatricule", created.getMatricule());

      return "auth/register-success";

    } catch (EmailAlreadyUsedException e) {

      /*
       * L'email existe déjà dans la base de données.
       */
      model.addAttribute("emailError", "Cet email est déjà utilisé.");

      return "auth/register";
    }
  }

  @PostMapping("/register/teacher")
  public String registerTeacher(
      @Valid @ModelAttribute("teacherDto") CreateTeacherDTO teacherDto,
      BindingResult bindingResult,
      Model model) {

    /*
     * On prépare le DTO étudiant car la page register.html
     * contient également le formulaire étudiant.
     */
    model.addAttribute("studentDto", CreateStudentDTO.builder().build());

    /*
     * On indique à la page que l'onglet enseignant doit rester actif
     * lorsque le formulaire contient une erreur.
     */
    model.addAttribute("activeTab", "teacher");

    /*
     * Si la validation échoue, on retourne sur la page d'inscription.
     */
    if (bindingResult.hasErrors()) {
      return "auth/register";
    }

    /*
     * Si les données sont valides, on crée le compte enseignant.
     */
    try {

      var created = teacherService.create(teacherDto);

      /*
       * Informations utilisées par register-success.html.
       */
      model.addAttribute("successRole", "Teacher");

      model.addAttribute("successMatricule", created.getMatricule());

      return "auth/register-success";

    } catch (EmailAlreadyUsedException e) {

      /*
       * L'email existe déjà dans la base de données.
       */
      model.addAttribute("emailError", "Cet email est déjà utilisé.");

      return "auth/register";
    }
  }
}
