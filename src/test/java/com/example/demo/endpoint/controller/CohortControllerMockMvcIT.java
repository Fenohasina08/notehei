package com.example.demo.endpoint.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.repository.CohortRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Exercises the {@code @PreAuthorize} role gate declared in {@code SecurityConfig}'s API filter
 * chain (not method security alone), which is why this builds MockMvc from the full
 * WebApplicationContext (springSecurity() applied) rather than a {@code @WebMvcTest} slice.
 */
class CohortControllerMockMvcIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(
                org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
                    .springSecurity())
            .build();
    cohortRepository.deleteAll();
  }

  @Test
  void anonymous_request_is_rejected_with_401() throws Exception {
    mockMvc.perform(get("/cohorts")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void student_can_read_the_cohort_list() throws Exception {
    mockMvc.perform(get("/cohorts")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void student_cannot_create_a_cohort() throws Exception {
    mockMvc
        .perform(
            post("/cohorts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("entryYear", 2040))))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_can_create_a_cohort() throws Exception {
    mockMvc
        .perform(
            post("/cohorts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("entryYear", 2041))))
        .andExpect(status().isCreated());
  }
}
