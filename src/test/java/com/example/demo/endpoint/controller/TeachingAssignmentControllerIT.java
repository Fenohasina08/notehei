package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JGroup;
import com.example.demo.entity.JTeacher;
import com.example.demo.repository.CohortRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

class TeachingAssignmentControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private CohortRepository cohortRepository;

  private UUID teacherId;
  private UUID courseId;
  private UUID groupId;

  @BeforeEach
  void setUp() {
    teachingAssignmentRepository.deleteAll();
    teacherRepository.deleteAll();
    courseRepository.deleteAll();
    groupRepository.deleteAll();
    cohortRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2043).build());

    groupId =
        groupRepository
            .save(JGroup.builder().reference("TA-IT").cohortId(cohort.getId()).build())
            .getId();

    courseId =
        courseRepository
            .save(
                JCourse.builder()
                    .reference("TA-IT-" + UUID.randomUUID().toString().substring(0, 8))
                    .title("Integration test course")
                    .coefficient(new BigDecimal("1.00"))
                    .build())
            .getId();

    teacherId =
        teacherRepository
            .save(
                JTeacher.builder()
                    .firstName("Tiana")
                    .lastName("Rakoto")
                    .email("ta-it-" + UUID.randomUUID() + "@notehei.local")
                    .password("secret")
                    .address("Antananarivo")
                    .matricule("TCH" + (int) (Math.random() * 90000 + 10000))
                    .build())
            .getId();
  }

  @Test
  void anonymous_request_is_rejected() {
    var requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());

    restTemplate.getRestTemplate().setRequestFactory(requestFactory);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teaching-assignments", HttpMethod.POST, requestBody(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private HttpEntity<Map<String, String>> requestBody() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    var body =
        Map.of(
            "teacherId", teacherId.toString(),
            "courseId", courseId.toString(),
            "groupId", groupId.toString());

    return new HttpEntity<>(body, headers);
  }
}
