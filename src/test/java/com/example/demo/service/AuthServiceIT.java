package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entity.JAdmin;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTeacher;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceIT extends FacadeIT {

  @Autowired private AuthService authService;

  @Autowired private AdminRepository adminRepository;

  @Autowired private StudentRepository studentRepository;

  @Autowired private TeacherRepository teacherRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    adminRepository.deleteAll();
    studentRepository.deleteAll();
    teacherRepository.deleteAll();
  }

  @Test
  void login_shouldAuthenticateAdminSuccessfully() {
    var admin =
        adminRepository.save(
            JAdmin.builder()
                .firstName("Admin")
                .lastName("Test")
                .email("admin-" + UUID.randomUUID() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .address("Antananarivo")
                .build());

    var request = LoginRequestDTO.builder().email(admin.getEmail()).password("password123").build();

    var result = authService.login(request);

    assertThat(result).isNotNull();
    assertThat(result.getToken()).isNotBlank();
    assertThat(result.getId()).isEqualTo(admin.getId());
    assertThat(result.getEmail()).isEqualTo(admin.getEmail());
    assertThat(result.getFirstName()).isEqualTo("Admin");
    assertThat(result.getLastName()).isEqualTo("Test");
    assertThat(result.getRole()).isEqualTo("ADMIN");
  }

  @Test
  void login_shouldAuthenticateStudentSuccessfully() {
    var student =
        studentRepository.save(
            JStudent.builder()
                .firstName("Student")
                .lastName("Test")
                .email("student-" + UUID.randomUUID() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .matricule(
                    "STD25" + UUID.randomUUID().toString().replaceAll("\\D", "").substring(0, 5))
                .build());

    var request =
        LoginRequestDTO.builder().email(student.getEmail()).password("password123").build();

    var result = authService.login(request);

    assertThat(result).isNotNull();
    assertThat(result.getToken()).isNotBlank();
    assertThat(result.getId()).isEqualTo(student.getId());
    assertThat(result.getEmail()).isEqualTo(student.getEmail());
    assertThat(result.getFirstName()).isEqualTo("Student");
    assertThat(result.getLastName()).isEqualTo("Test");
    assertThat(result.getRole()).isEqualTo("STUDENT");
  }

  @Test
  void login_shouldAuthenticateTeacherSuccessfully() {
    var teacher =
        teacherRepository.save(
            JTeacher.builder()
                .firstName("Teacher")
                .lastName("Test")
                .email("teacher-" + UUID.randomUUID() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .address("Antananarivo")
                .matricule(
                    "TCH" + UUID.randomUUID().toString().replaceAll("\\D", "").substring(0, 6))
                .build());

    var request =
        LoginRequestDTO.builder().email(teacher.getEmail()).password("password123").build();

    var result = authService.login(request);

    assertThat(result).isNotNull();
    assertThat(result.getToken()).isNotBlank();
    assertThat(result.getId()).isEqualTo(teacher.getId());
    assertThat(result.getEmail()).isEqualTo(teacher.getEmail());
    assertThat(result.getFirstName()).isEqualTo("Teacher");
    assertThat(result.getLastName()).isEqualTo("Test");
    assertThat(result.getRole()).isEqualTo("TEACHER");
  }

  @Test
  void login_shouldRejectUnknownEmail() {
    var request =
        LoginRequestDTO.builder()
            .email("unknown-" + UUID.randomUUID() + "@test.com")
            .password("password123")
            .build();

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void login_shouldRejectIncorrectAdminPassword() {
    var admin =
        adminRepository.save(
            JAdmin.builder()
                .firstName("Admin")
                .lastName("Test")
                .email("admin-" + UUID.randomUUID() + "@test.com")
                .password(passwordEncoder.encode("correctPassword"))
                .address("Antananarivo")
                .build());

    var request =
        LoginRequestDTO.builder().email(admin.getEmail()).password("wrongPassword").build();

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void login_shouldRejectIncorrectStudentPassword() {
    var student =
        studentRepository.save(
            JStudent.builder()
                .firstName("Student")
                .lastName("Test")
                .email("student-" + UUID.randomUUID() + "@test.com")
                .password(passwordEncoder.encode("correctPassword"))
                .matricule(
                    "STD25" + UUID.randomUUID().toString().replaceAll("\\D", "").substring(0, 5))
                .build());

    var request =
        LoginRequestDTO.builder().email(student.getEmail()).password("wrongPassword").build();

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void login_shouldRejectIncorrectTeacherPassword() {
    var teacher =
        teacherRepository.save(
            JTeacher.builder()
                .firstName("Teacher")
                .lastName("Test")
                .email("teacher-" + UUID.randomUUID() + "@test.com")
                .password(passwordEncoder.encode("correctPassword"))
                .address("Antananarivo")
                .matricule(
                    "TCH" + UUID.randomUUID().toString().replaceAll("\\D", "").substring(0, 6))
                .build());

    var request =
        LoginRequestDTO.builder().email(teacher.getEmail()).password("wrongPassword").build();

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);
  }
}
