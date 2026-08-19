package com.example.demo.endpoint.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JSemester;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.CohortRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.SemesterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class CourseUnitControllerMockMvcIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private CourseUnitRepository courseUnitRepository;
  @Autowired private SemesterRepository semesterRepository;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;
  private UUID semesterId;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

    courseUnitRepository.deleteAll();
    semesterRepository.deleteAll();
    cohortRepository.deleteAll();
    academicYearRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2042).build());
    var academicYear =
        academicYearRepository.save(
            JAcademicYear.builder().name("CU-MVC-2042").startYear(2042).endYear(2043).build());
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

  @Test
  @WithMockUser(roles = "TEACHER")
  void teacher_cannot_create_a_course_unit() throws Exception {
    mockMvc
        .perform(
            post("/course-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "code",
                            "UE-MVC1",
                            "name",
                            "Mockmvc course unit",
                            "credits",
                            6,
                            "semesterId",
                            semesterId.toString()))))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_can_create_a_course_unit_within_the_credit_ceiling() throws Exception {
    mockMvc
        .perform(
            post("/course-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "code",
                            "UE-MVC2",
                            "name",
                            "Mockmvc course unit",
                            "credits",
                            10,
                            "semesterId",
                            semesterId.toString()))))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_cannot_create_a_course_unit_that_exceeds_the_semester_credit_ceiling()
      throws Exception {
    mockMvc
        .perform(
            post("/course-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "code",
                            "UE-MVC3",
                            "name",
                            "Too many credits",
                            "credits",
                            31,
                            "semesterId",
                            semesterId.toString()))))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void anonymous_cannot_list_course_units() throws Exception {
    mockMvc
        .perform(get("/course-units").param("semesterId", semesterId.toString()))
        .andExpect(status().isUnauthorized());
  }
}
