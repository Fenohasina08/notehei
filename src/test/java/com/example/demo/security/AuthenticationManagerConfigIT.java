package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAdmin;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTeacher;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthenticationManagerConfigIT extends FacadeIT {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private StudentRepository studentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private AdminRepository adminRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    studentRepository.deleteAll();
    teacherRepository.deleteAll();
    adminRepository.deleteAll();
  }

  @Test
  void authenticates_a_student_account() {
    studentRepository.save(
        JStudent.builder()
            .firstName("Ny")
            .lastName("Aina")
            .email("student.auth@notehei.local")
            .password(passwordEncoder.encode("secret123"))
            .address("Antananarivo")
            .matricule("STD25001")
            .build());

    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken("student.auth@notehei.local", "secret123"));

    assertThat(authentication.isAuthenticated()).isTrue();
    var principal = (SecurityUser) authentication.getPrincipal();
    assertThat(principal.getRole()).isEqualTo(Role.STUDENT);
  }

  @Test
  void authenticates_a_teacher_account() {
    teacherRepository.save(
        JTeacher.builder()
            .firstName("Tiana")
            .lastName("Rakoto")
            .email("teacher.auth@notehei.local")
            .password(passwordEncoder.encode("secret123"))
            .address("Antananarivo")
            .matricule("TCH001")
            .build());

    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken("teacher.auth@notehei.local", "secret123"));

    assertThat(authentication.isAuthenticated()).isTrue();
    var principal = (SecurityUser) authentication.getPrincipal();
    assertThat(principal.getRole()).isEqualTo(Role.TEACHER);
  }

  @Test
  void authenticates_an_admin_account() {
    adminRepository.save(
        JAdmin.builder()
            .firstName("Sarah")
            .lastName("Admin")
            .email("admin.auth@notehei.local")
            .password(passwordEncoder.encode("secret123"))
            .address("Antananarivo")
            .build());

    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken("admin.auth@notehei.local", "secret123"));

    assertThat(authentication.isAuthenticated()).isTrue();
    var principal = (SecurityUser) authentication.getPrincipal();
    assertThat(principal.getRole()).isEqualTo(Role.ADMIN);
  }

  @Test
  void rejects_unknown_email_across_all_account_types() {
    assertThatThrownBy(
            () ->
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        "nobody-" + UUID.randomUUID() + "@notehei.local", "whatever")))
        .isInstanceOf(BadCredentialsException.class);
  }

  @Test
  void rejects_wrong_password() {
    studentRepository.save(
        JStudent.builder()
            .firstName("Ny")
            .lastName("Aina")
            .email("student.badpass@notehei.local")
            .password(passwordEncoder.encode("secret123"))
            .address("Antananarivo")
            .matricule("STD25002")
            .build());

    assertThatThrownBy(
            () ->
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        "student.badpass@notehei.local", "wrong-password")))
        .isInstanceOf(BadCredentialsException.class);
  }
}
