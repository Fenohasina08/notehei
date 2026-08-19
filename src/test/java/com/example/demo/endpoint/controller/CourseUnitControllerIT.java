package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JSemester;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.CohortRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.SemesterRepository;
import java.util.UUID;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

class CourseUnitControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private CourseUnitRepository courseUnitRepository;
  @Autowired private SemesterRepository semesterRepository;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private AcademicYearRepository academicYearRepository;

  private UUID semesterId;

  @BeforeEach
  void setUp() {
    courseUnitRepository.deleteAll();
    semesterRepository.deleteAll();
    cohortRepository.deleteAll();
    academicYearRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2042).build());

    var academicYear =
        academicYearRepository.save(
            JAcademicYear.builder().name("CU-IT-2042").startYear(2042).endYear(2043).build());

    semesterId =
        semesterRepository
            .save(
                JSemester.builder()
                    .number(1)
                    .cohortId(cohort.getId())
                    .academicYearId(academicYear.getId())
                    .build())
            .getId();
  }

  @AfterEach
  void tearDown() {
    courseUnitRepository.deleteAll();
    semesterRepository.deleteAll();
    cohortRepository.deleteAll();
    academicYearRepository.deleteAll();
  }

  @Test
  void anonymous_cannot_list_course_units() {
    restTemplate
        .getRestTemplate()
        .setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/course-units?semesterId=" + semesterId, HttpMethod.GET, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
