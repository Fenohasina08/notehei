package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.repository.StudentRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.Role;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.client.TestRestTemplate;

class GroupMembershipControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private StudentRepository studentRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  private JStudent studentA;
  private JStudent studentB;

  @BeforeEach
  void setUp() {
    studentA = createStudent("gm-self-a@notehei.local", "STD25301");
    studentB = createStudent("gm-self-b@notehei.local", "STD25302");
  }

  private JStudent createStudent(String email, String matricule) {
    return studentRepository.save(
        JStudent.builder()
            .firstName("Ny")
            .lastName("Aina")
            .email(email)
            .password(passwordEncoder.encode("secret123"))
            .address("Antananarivo")
            .matricule(matricule)
            .build());
  }

  private HttpHeaders bearerFor(JStudent student) {
    var token = jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT);
    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    return headers;
  }

  @Test
  void a_student_can_read_their_own_membership_history() {
    var response =
        restTemplate.exchange(
            "/group-memberships/student/" + studentA.getId(),
            HttpMethod.GET,
            new HttpEntity<>(bearerFor(studentA)),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void a_student_cannot_read_another_students_membership_history() {
    var response =
        restTemplate.exchange(
            "/group-memberships/student/" + studentB.getId(),
            HttpMethod.GET,
            new HttpEntity<>(bearerFor(studentA)),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void an_unknown_student_id_is_still_forbidden_for_a_student_caller() {
    var response =
        restTemplate.exchange(
            "/group-memberships/student/" + UUID.randomUUID(),
            HttpMethod.GET,
            new HttpEntity<>(bearerFor(studentA)),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
