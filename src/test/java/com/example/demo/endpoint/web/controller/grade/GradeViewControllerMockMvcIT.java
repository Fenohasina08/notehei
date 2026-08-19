package com.example.demo.endpoint.web.controller.grade;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JTeacher;
import com.example.demo.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * {@code GradeViewController} resolves {@code @AuthenticationPrincipal SecurityUser}, so
 * {@code @WithMockUser} (which sets a generic Spring Security {@code User} principal) can't be used
 * here — it would produce a ClassCastException on the controller's argument resolution.
 * {@code @WithUserDetails} loads a real {@code SecurityUser} through {@code
 * TeacherUserDetailsService}, exactly as the web login flow does.
 */
class GradeViewControllerMockMvcIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

    teacherRepository
        .findByEmail("grade-view-mvc@notehei.local")
        .orElseGet(
            () ->
                teacherRepository.save(
                    JTeacher.builder()
                        .firstName("Tiana")
                        .lastName("Rakoto")
                        .email("grade-view-mvc@notehei.local")
                        .password(passwordEncoder.encode("secret123"))
                        .address("Antananarivo")
                        .matricule("TCH77001")
                        .build()));
  }

  @Test
  void anonymous_access_redirects_to_login() throws Exception {
    mockMvc.perform(get("/screens/grades")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void student_cannot_access_the_teacher_grade_entry_form() throws Exception {
    mockMvc.perform(get("/screens/grades")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails(
      value = "grade-view-mvc@notehei.local",
      userDetailsServiceBeanName = "teacherUserDetailsService")
  void teacher_can_access_the_grade_entry_form() throws Exception {
    mockMvc.perform(get("/screens/grades")).andExpect(status().isOk());
  }
}
