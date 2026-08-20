package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

class WebControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void testIndexPage() {
    ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);

    printResponse("/", response);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void testDashboardPage() {
    ResponseEntity<String> response = restTemplate.getForEntity("/dashboard", String.class);

    printResponse("/dashboard", response);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void testTranscriptsPage() {
    ResponseEntity<String> response = restTemplate.getForEntity("/transcripts", String.class);

    printResponse("/transcripts", response);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void testGradesPage() {
    ResponseEntity<String> response = restTemplate.getForEntity("/grades", String.class);

    printResponse("/grades", response);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
  }

  private void printResponse(String path, ResponseEntity<String> response) {
    System.out.println();
    System.out.println("========== " + path + " ==========");
    System.out.println("STATUS   : " + response.getStatusCode());
    System.out.println("LOCATION : " + response.getHeaders().getLocation());
    System.out.println("BODY     : " + response.getBody());
    System.out.println("================================");
  }
}
