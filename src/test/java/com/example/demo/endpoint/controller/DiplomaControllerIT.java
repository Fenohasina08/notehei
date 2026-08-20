package com.example.demo.endpoint.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTranscript;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.CohortRepository;
import com.example.demo.repository.DiplomaRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TranscriptRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
class DiplomaControllerIT extends FacadeIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private StudentRepository studentRepository;
  @Autowired private SemesterRepository semesterRepository;
  @Autowired private TranscriptRepository transcriptRepository;
  @Autowired private DiplomaRepository diplomaRepository;
  @Autowired private CohortRepository cohortRepository;
  @Autowired private AcademicYearRepository academicYearRepository;

  @BeforeEach
  void setUp() {
    diplomaRepository.deleteAll();
    transcriptRepository.deleteAll();
    semesterRepository.deleteAll();
    studentRepository.deleteAll();
    academicYearRepository.deleteAll();
    cohortRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    diplomaRepository.deleteAll();
    transcriptRepository.deleteAll();
    semesterRepository.deleteAll();
    studentRepository.deleteAll();
    academicYearRepository.deleteAll();
    cohortRepository.deleteAll();
  }

  @Test
  void student_can_get_diploma_when_all_semesters_validated() throws Exception {
    var cohort = cohortRepository.save(JCohort.builder().entryYear(2024).build());
    var academicYear =
        academicYearRepository.save(
            JAcademicYear.builder().name("DIPLOMA-IT-2024").startYear(2024).endYear(2025).build());

    var student =
        studentRepository.save(
            JStudent.builder()
                .firstName("Fenohasina")
                .lastName("Rafanomezana")
                .email("fenohasinaherimamy@gmail.com")
                .password("$2a$10$Nw3ljzLgI/Y5pE5M12E1ZO0j7HLPAnJR8eNgWsZEocpnQcioGtOJG")
                .matricule("STD26041")
                .birthdate(LocalDate.of(2002, 5, 20))
                .build());

    for (int i = 1; i <= 6; i++) {
      var semester =
          semesterRepository.save(
              JSemester.builder()
                  .number(i)
                  .cohortId(cohort.getId())
                  .academicYearId(academicYear.getId())
                  .build());

      transcriptRepository.save(
          JTranscript.builder()
              .studentId(student.getId())
              .semesterId(semester.getId())
              .status("VALIDATED")
              .build());
    }

    mockMvc
        .perform(post("/students/" + student.getId() + "/diploma/evaluate"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("VALIDATED"))
        .andExpect(jsonPath("$.studentId").value(student.getId().toString()));
  }

  @Test
  void student_cannot_get_diploma_when_semesters_missing() throws Exception {
    var cohort = cohortRepository.save(JCohort.builder().entryYear(2024).build());
    var academicYear =
        academicYearRepository.save(
            JAcademicYear.builder().name("DIPLOMA-IT-2025").startYear(2024).endYear(2025).build());

    var student =
        studentRepository.save(
            JStudent.builder()
                .firstName("Ny")
                .lastName("Aina")
                .email("student.demo@notehei.local")
                .password("$2a$10$E58I7Z0hRKAqoVQSQhWjqeclRzVKBLwnEIspr.artcCUFD40LQrdG")
                .matricule("STD26016")
                .birthdate(LocalDate.of(2003, 5, 12))
                .build());

    // Validation uniquement de S1 à S3
    for (int i = 1; i <= 3; i++) {
      var semester =
          semesterRepository.save(
              JSemester.builder()
                  .number(i)
                  .cohortId(cohort.getId())
                  .academicYearId(academicYear.getId())
                  .build());

      transcriptRepository.save(
          JTranscript.builder()
              .studentId(student.getId())
              .semesterId(semester.getId())
              .status("VALIDATED")
              .build());
    }

    mockMvc
        .perform(post("/students/" + student.getId() + "/diploma/evaluate"))
        .andExpect(status().isInternalServerError());
  }
}
