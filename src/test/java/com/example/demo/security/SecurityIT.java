package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

class SecurityIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private StudentRepository studentRepository;
  @Autowired private AdminRepository adminRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @LocalServerPort private int port;

  private static final String RAW_PASSWORD = "secret123";

  @BeforeEach
  void setUp() {
    studentRepository.deleteAll();
  }

  private JStudent createStudent(String email) {
    return studentRepository.save(
        JStudent.builder()
            .firstName("Ny")
            .lastName("Aina")
            .email(email)
            .password(passwordEncoder.encode(RAW_PASSWORD))
            .address("Antananarivo")
            .matricule("STD25" + String.valueOf(Math.abs(email.hashCode())).substring(0, 3))
            .build());
  }

  // --- /auth/login (REST, API chain) -----------------------------------------------------

  @Test
  void login_with_correct_credentials_returns_a_token() {
    var student = createStudent("sec.login.ok@notehei.local");

    var response =
        restTemplate.postForEntity(
            "/auth/login",
            new LoginRequestDTO(student.getEmail(), RAW_PASSWORD),
            LoginResponseDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getToken()).isNotBlank();
    assertThat(response.getBody().getRole()).isEqualTo("STUDENT");
  }

  @Test
  void login_with_wrong_password_returns_401() {
    var student = createStudent("sec.login.badpass@notehei.local");

    var response =
        restTemplate.postForEntity(
            "/auth/login",
            new LoginRequestDTO(student.getEmail(), "not-the-right-password"),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void login_with_unknown_email_returns_401() {
    var response =
        restTemplate.postForEntity(
            "/auth/login",
            new LoginRequestDTO("nobody-" + UUID.randomUUID() + "@notehei.local", "whatever"),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  // --- Protected REST endpoints (API chain, JWT) ------------------------------------------

  @Test
  void accessing_a_protected_endpoint_without_a_token_returns_401_json() {
    var response = restTemplate.getForEntity("/admins/" + UUID.randomUUID(), ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().code()).isEqualTo("UNAUTHORIZED");
  }

  @Test
  void accessing_a_protected_endpoint_with_a_malformed_token_returns_401() {
    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer not-a-real-jwt");

    var response =
        restTemplate.exchange(
            "/admins/" + UUID.randomUUID(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void accessing_a_protected_endpoint_with_an_expired_token_returns_401() {
    var student = createStudent("sec.expired@notehei.local");

    var signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    var expiredToken =
        io.jsonwebtoken.Jwts.builder()
            .subject(student.getEmail())
            .claim("id", student.getId().toString())
            .claim("role", Role.STUDENT.name())
            .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2))
            .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
            .signWith(signingKey)
            .compact();

    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + expiredToken);

    var response =
        restTemplate.exchange(
            "/students/" + student.getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void a_student_token_cannot_access_admin_only_routes() {
    var student = createStudent("sec.forbidden@notehei.local");
    var token = jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT);

    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    var response =
        restTemplate.exchange(
            "/admins/" + UUID.randomUUID(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().code()).isEqualTo("FORBIDDEN");
  }

  @Test
  void a_student_can_access_their_own_profile() {
    var student = createStudent("sec.self@notehei.local");
    var token = jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT);

    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    var response =
        restTemplate.exchange(
            "/students/" + student.getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  // --- Public endpoints --------------------------------------------------------------------

  @Test
  void ping_is_public() {
    var response = restTemplate.getForEntity("/ping", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  // --- Web chain (Thymeleaf) ----------------------------------------------------------------

  @Test
  void login_page_is_publicly_reachable() {
    var httpClient = HttpClients.custom().disableRedirectHandling().build();
    var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    var noRedirectRestTemplate = new RestTemplate(requestFactory);

    var response =
        noRedirectRestTemplate.getForEntity("http://localhost:" + port + "/login", String.class);

    assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.FOUND);
  }
}
