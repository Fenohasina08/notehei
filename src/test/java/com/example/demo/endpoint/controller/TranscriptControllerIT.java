package com.example.demo.endpoint.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.demo.conf.FacadeIT;
import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.TranscriptRequested;
import com.example.demo.entity.JAdmin;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.model.Transcript;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.Role;
import java.util.Collection;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class TranscriptControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private JwtService jwtService;

  @Autowired private StudentRepository studentRepository;

  @Autowired private AdminRepository adminRepository;

  @Autowired private TranscriptRepository transcriptRepository;

  @MockBean private EventProducer<TranscriptRequested> eventProducer;

  @BeforeEach
  void setUp() {
    cleanDatabase();
  }

  @Test
  void request_asStudentForSelf_shouldCreatePendingTranscriptAndProduceEvent() {
    var student = createStudent();
    var semesterId = createSemester();
    var headers =
        bearer(jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT));

    var response =
        restTemplate.exchange(
            "/transcripts/student/{studentId}/semester/{semesterId}",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            Transcript.class,
            student.getId(),
            semesterId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.studentId()).isEqualTo(student.getId());
    assertThat(body.semesterId()).isEqualTo(semesterId);
    assertThat(body.status()).isEqualTo("PENDING");
    assertThat(body.id()).isNotNull();

    assertThat(transcriptRepository.findById(body.id())).isPresent();

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Collection<TranscriptRequested>> captor =
        ArgumentCaptor.forClass(Collection.class);
    verify(eventProducer, times(1)).accept(captor.capture());
    var producedEvents = captor.getValue();
    assertThat(producedEvents).hasSize(1);
    var event = producedEvents.iterator().next();
    assertThat(event.getTranscriptId()).isEqualTo(body.id());
    assertThat(event.getStudentId()).isEqualTo(student.getId());
    assertThat(event.getSemesterId()).isEqualTo(semesterId);
  }

  @Test
  void request_asAdmin_forAnyStudent_shouldCreatePendingTranscript() {
    var student = createStudent();
    var admin = createAdmin();
    var semesterId = createSemester();
    var headers = bearer(jwtService.generateToken(admin.getId(), admin.getEmail(), Role.ADMIN));

    var response =
        restTemplate.exchange(
            "/transcripts/student/{studentId}/semester/{semesterId}",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            Transcript.class,
            student.getId(),
            semesterId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().status()).isEqualTo("PENDING");
    verify(eventProducer, times(1)).accept(any());
  }

  @Test
  void request_asStudentForAnotherStudent_shouldReturn422NotFoundNorForbidden() {
    var student = createStudent();
    var otherStudent = createStudent();
    var semesterId = createSemester();
    var headers =
        bearer(jwtService.generateToken(student.getId(), student.getEmail(), Role.STUDENT));

    var response =
        restTemplate.exchange(
            "/transcripts/student/{studentId}/semester/{semesterId}",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            ErrorResponse.class,
            otherStudent.getId(),
            semesterId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody().code()).isEqualTo("TRANSCRIPT_VALIDATION_FAILED");
    verifyNoInteractions(eventProducer);
  }

  @Test
  void request_withTeacherToken_shouldReturn403() {
    var student = createStudent();
    var semesterId = createSemester();
    var headers =
        bearer(jwtService.generateToken(UUID.randomUUID(), "teacher@test.com", Role.TEACHER));

    var response =
        restTemplate.exchange(
            "/transcripts/student/{studentId}/semester/{semesterId}",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            ErrorResponse.class,
            student.getId(),
            semesterId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    verifyNoInteractions(eventProducer);
  }

  @Test
  void request_withoutToken_shouldReturn401() {
    var student = createStudent();
    var semesterId = createSemester();

    var response =
        restTemplate.exchange(
            "/transcripts/student/{studentId}/semester/{semesterId}",
            HttpMethod.POST,
            new HttpEntity<>(new HttpHeaders()),
            ErrorResponse.class,
            student.getId(),
            semesterId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verifyNoInteractions(eventProducer);
  }

  @Test
  void listForStudent_shouldReturnPersistedTranscripts() {
    var student = createStudent();
    var semesterOne = createSemester();
    var semesterTwo = createSemester();
    persistTranscript(student.getId(), semesterOne, "PENDING");
    persistTranscript(student.getId(), semesterTwo, "GENERATED");

    // Note: /transcripts/student/{id} has no @PreAuthorize and no ownership check in
    // SecurityConfig (falls under anyRequest().authenticated()) - ANY authenticated account,
    // regardless of role or identity, can list ANY student's transcripts today. This test
    // documents that current (over-permissive) behaviour rather than an intended contract.
    var headers =
        bearer(jwtService.generateToken(UUID.randomUUID(), "someone@test.com", Role.TEACHER));

    var response =
        restTemplate.exchange(
            "/transcripts/student/{studentId}",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            Transcript[].class,
            student.getId());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  private HttpHeaders bearer(String token) {
    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    return headers;
  }

  private JStudent createStudent() {
    return studentRepository.save(
        JStudent.builder()
            .firstName("Student")
            .lastName("Test")
            .email("student-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .matricule(
                "STD26" + java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 999999))
            .build());
  }

  private JAdmin createAdmin() {
    return adminRepository.save(
        JAdmin.builder()
            .firstName("Admin")
            .lastName("Test")
            .email("admin-" + UUID.randomUUID() + "@test.com")
            .password("password")
            .address("Antananarivo")
            .build());
  }

  private UUID createSemester() {
    var cohortId = UUID.randomUUID();
    var academicYearId = UUID.randomUUID();
    var semesterId = UUID.randomUUID();

    jdbcTemplate()
        .update(
            "INSERT INTO cohort (id, entry_year) VALUES (?, ?)",
            cohortId,
            java.util.concurrent.ThreadLocalRandom.current().nextInt(2000, 1000000));
    jdbcTemplate()
        .update(
            "INSERT INTO academic_year (id, name, start_year, end_year) VALUES (?, ?, ?, ?)",
            academicYearId,
            "AY-" + UUID.randomUUID().toString().substring(0, 6),
            2026,
            2027);
    jdbcTemplate()
        .update(
            "INSERT INTO semester (id, number, cohort_id, academic_year_id) VALUES (?, ?, ?, ?)",
            semesterId,
            1,
            cohortId,
            academicYearId);
    return semesterId;
  }

  private void persistTranscript(UUID studentId, UUID semesterId, String status) {
    jdbcTemplate()
        .update(
            "INSERT INTO transcript (id, student_id, semester_id, status) VALUES (?, ?, ?, ?)",
            UUID.randomUUID(),
            studentId,
            semesterId,
            status);
  }
}
