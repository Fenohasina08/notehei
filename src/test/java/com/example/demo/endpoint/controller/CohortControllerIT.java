package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAdmin;
import com.example.demo.entity.JStudent;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CohortRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.Role;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CohortControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private CohortRepository cohortRepository;

  @Autowired private SemesterRepository semesterRepository;

  @Autowired private StudentRepository studentRepository;

  @Autowired private AdminRepository adminRepository;

  @Autowired private JwtService jwtService;

  @BeforeEach
  void setUp() {
    cleanDatabase();
  }

  @Test
  void anonymous_request_is_rejected_with_401() {
    ResponseEntity<String> response = restTemplate.getForEntity("/cohorts", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void student_can_read_the_cohort_list() {
    JStudent student = createStudent();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(
        jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT));

    ResponseEntity<String> response =
        restTemplate.exchange("/cohorts", HttpMethod.GET, new HttpEntity<>(headers), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void student_cannot_create_a_cohort() {
    JStudent student = createStudent();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(
        jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT));

    HttpEntity<Map<String, Integer>> request = new HttpEntity<>(Map.of("entryYear", 2040), headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/cohorts", HttpMethod.POST, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void admin_can_create_a_cohort() {
    JAdmin admin = createAdmin();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtService.generateToken(admin.getId(), admin.getEmail(), Role.ADMIN));

    HttpEntity<Map<String, Integer>> request = new HttpEntity<>(Map.of("entryYear", 2041), headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/cohorts", HttpMethod.POST, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  private JStudent createStudent() {
    String matricule =
        "STD"
            + String.format(
                "%05d", Math.floorMod(UUID.randomUUID().getMostSignificantBits(), 100000));

    return studentRepository.save(
        JStudent.builder()
            .firstName("Test")
            .lastName("Student")
            .email("student-" + UUID.randomUUID() + "@test.local")
            .password("test-password")
            .address("Antananarivo")
            .matricule(matricule)
            .build());
  }

  private JAdmin createAdmin() {
    return adminRepository.save(
        JAdmin.builder()
            .firstName("Test")
            .lastName("Admin")
            .email("admin-" + UUID.randomUUID() + "@test.local")
            .password("test-password")
            .address("Antananarivo")
            .build());
  }
}
