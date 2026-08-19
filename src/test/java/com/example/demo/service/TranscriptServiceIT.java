package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.conf.FacadeIT;
import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.TranscriptRequestedEvent;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import com.example.demo.model.Transcript;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TranscriptRepository;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TranscriptServiceIT extends FacadeIT {

  @Autowired private TranscriptService transcriptService;
  @Autowired private StudentRepository studentRepository;
  @Autowired private SemesterRepository semesterRepository;
  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private TranscriptRepository transcriptRepository;
  @Autowired private EntityManager entityManager;

  @MockBean private EventProducer<TranscriptRequestedEvent> eventProducer;

  @Test
  void requestTranscript_shouldPersistAsPending() {
    JStudent student = new JStudent();
    student.setId(UUID.randomUUID());
    student.setFirstName("Alice");
    student.setLastName("Smith");
    student.setMatricule("STD24191");
    student.setEmail("alice.smith@test.com");
    student.setPassword("TestPassword123!");
    studentRepository.saveAndFlush(student);

    JAcademicYear academicYear = new JAcademicYear();
    academicYear.setId(UUID.randomUUID());
    academicYear.setName("2024-2025");
    academicYear.setStartYear(2024);
    academicYear.setEndYear(2025);
    academicYearRepository.saveAndFlush(academicYear);

    UUID cohortId = UUID.randomUUID();
    entityManager
        .createNativeQuery("INSERT INTO cohort (id, entry_year) VALUES (:id, :entryYear)")
        .setParameter("id", cohortId)
        .setParameter("entryYear", 2024)
        .executeUpdate();

    JSemester semester = new JSemester();
    semester.setId(UUID.randomUUID());
    semester.setNumber(1);
    semester.setCohortId(cohortId);
    semester.setAcademicYearId(academicYear.getId());
    semesterRepository.saveAndFlush(semester);

    UUID studentId = student.getId();
    UUID semesterId = semester.getId();

    Transcript result =
        transcriptService.requestTranscript(studentId, semesterId, studentId, false);

    assertNotNull(result);
    assertEquals("PENDING", result.status());
    assertEquals(1, transcriptRepository.findByStudentId(studentId).size());
  }
}
