package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.repository.StudentRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.Role;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

class GroupMembershipControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private StudentRepository studentRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  private static final AtomicInteger MATRICULE_SEQUENCE = new AtomicInteger(10000);

  private JStudent studentA;
  private JStudent studentB;

  @BeforeEach
  void setUp() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);

    studentA = createStudent("gm-self-a-" + suffix + "@notehei.local", generateMatricule());

    studentB = createStudent("gm-self-b-" + suffix + "@notehei.local", generateMatricule());
  }

  @AfterEach
  void tearDown() {
    if (studentA != null) {
      studentRepository.deleteById(studentA.getId());
    }

    if (studentB != null) {
      studentRepository.deleteById(studentB.getId());
    }
  }

  private String generateMatricule() {
    return "STD" + MATRICULE_SEQUENCE.getAndIncrement();
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
    headers.setBearerAuth(token);
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
