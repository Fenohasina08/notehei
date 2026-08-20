package com.example.demo.endpoint.web.controller.home;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = "USER")
class HomeViewControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void testIndexPage() throws Exception {
    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(model().attributeExists("title"))
        .andExpect(model().attributeExists("message"));
  }

  @Test
  void testDashboardPage() throws Exception {
    mockMvc
        .perform(get("/dashboard"))
        .andExpect(status().isOk())
        .andExpect(view().name("dashboard"))
        .andExpect(model().attributeExists("title"))
        .andExpect(model().attributeExists("user"));
  }

  @Test
  void testTranscriptsPage() throws Exception {
    mockMvc
        .perform(get("/transcripts"))
        .andExpect(status().isOk())
        .andExpect(view().name("transcripts"))
        .andExpect(model().attributeExists("title"));
  }

  @Test
  void testGradesPage() throws Exception {
    mockMvc
        .perform(get("/grades"))
        .andExpect(status().isOk())
        .andExpect(view().name("grades"))
        .andExpect(model().attributeExists("title"));
  }
}
