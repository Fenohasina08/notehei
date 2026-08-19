package com.example.demo.endpoint.web.controller.cohort;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Regression test for a real gap found while writing this suite: admin Thymeleaf screens (cohort,
 * semester, group, program, course-unit, course, teaching-assignment, structure) had no
 * {@code @PreAuthorize} — the nav only *hid* the links from non-admins, direct URL access was wide
 * open to any authenticated user. Now fixed with a class-level
 * {@code @PreAuthorize("hasRole('ADMIN')")} on each of those ViewControllers.
 */
class CohortViewControllerMockMvcIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
  }

  @Test
  void anonymous_access_redirects_to_login() throws Exception {
    mockMvc.perform(get("/screens/cohorts")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void student_cannot_access_the_admin_screen() throws Exception {
    mockMvc.perform(get("/screens/cohorts")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void teacher_cannot_access_the_admin_screen() throws Exception {
    mockMvc.perform(get("/screens/cohorts")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_can_access_the_screen() throws Exception {
    mockMvc.perform(get("/screens/cohorts")).andExpect(status().isOk());
  }
}
