package com.example.demo.endpoint.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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

class TeachingAssignmentControllerMockMvcIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;
  private UUID teacherId;
  private UUID courseId;
  private UUID groupId;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

    teachingAssignmentRepository.deleteAll();
    teacherRepository.deleteAll();
    courseRepository.deleteAll();
    groupRepository.deleteAll();
    cohortRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2043).build());
    groupId =
        groupRepository
            .save(JGroup.builder().reference("TA-MVC").cohortId(cohort.getId()).build())
            .getId();
    courseId =
        courseRepository
            .save(
                JCourse.builder()
                    .reference("TA-MVC-" + UUID.randomUUID().toString().substring(0, 8))
                    .title("Mockmvc course")
                    .coefficient(new BigDecimal("1.00"))
                    .build())
            .getId();
    teacherId =
        teacherRepository
            .save(
                JTeacher.builder()
                    .firstName("Tiana")
                    .lastName("Rakoto")
                    .email("ta-mvc-" + UUID.randomUUID() + "@notehei.local")
                    .password("secret")
                    .address("Antananarivo")
                    .matricule("TCH" + (int) (Math.random() * 90000 + 10000))
                    .build())
            .getId();
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void student_cannot_create_a_teaching_assignment() throws Exception {
    mockMvc
        .perform(
            post("/teaching-assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "teacherId", teacherId.toString(),
                            "courseId", courseId.toString(),
                            "groupId", groupId.toString()))))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_can_create_a_teaching_assignment() throws Exception {
    mockMvc
        .perform(
            post("/teaching-assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "teacherId", teacherId.toString(),
                            "courseId", courseId.toString(),
                            "groupId", groupId.toString()))))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_cannot_create_the_same_assignment_twice() throws Exception {
    var body =
        objectMapper.writeValueAsString(
            Map.of(
                "teacherId", teacherId.toString(),
                "courseId", courseId.toString(),
                "groupId", groupId.toString()));

    mockMvc
        .perform(
            post("/teaching-assignments").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/teaching-assignments").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnprocessableEntity());
  }
}
