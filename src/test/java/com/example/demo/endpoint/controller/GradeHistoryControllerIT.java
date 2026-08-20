package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.model.GradeHistory;
import com.example.demo.security.JwtService;
import com.example.demo.security.Role;
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
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

class GradeHistoryControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private JwtService jwtService;

  @BeforeEach
  void setUp() {
    cleanDatabase();
    restTemplate
        .getRestTemplate()
        .setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
  }

  @Test
  void anonymous_get_grade_history_is_rejected_with_401() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/grade-history/" + UUID.randomUUID(), HttpMethod.GET, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void authenticated_admin_get_grade_history_returns_200_with_history() {
    var headers = new HttpHeaders();
    headers.set(
        "Authorization",
        "Bearer " + jwtService.generateToken(UUID.randomUUID(), "admin@test.com", Role.ADMIN));

    ResponseEntity<GradeHistory[]> response =
        restTemplate.exchange(
            "/grade-history/" + UUID.randomUUID(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GradeHistory[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }
}
