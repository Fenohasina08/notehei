package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import com.example.demo.entity.JTranscript;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.pdf.TranscriptPdfGenerator;
import com.example.demo.mapper.TranscriptMapper;
import com.example.demo.model.Transcript;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.validator.TranscriptValidator;
import java.io.File;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TranscriptServiceTest {

  @Mock private TranscriptRepository transcriptRepository;
  @Mock private TranscriptValidator transcriptValidator;
  @Mock private TranscriptMapper transcriptMapper;
  @Mock private TranscriptPdfGenerator transcriptPdfGenerator;
  @Mock private BucketComponent bucketComponent;
  @Mock private EventProducer<SendEmailRequested> eventProducer;
  @Mock private StudentRepository studentRepository;
  @Mock private SemesterRepository semesterRepository;
  @Mock private AcademicYearRepository academicYearRepository;

  @InjectMocks private TranscriptService transcriptService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void student_can_request_their_own_transcript() throws Exception {
    UUID studentId = UUID.randomUUID();
    UUID semesterId = UUID.randomUUID();
    UUID requesterId = studentId;

    JStudent student = new JStudent();
    student.setId(studentId);
    student.setEmail("student@test.com");

    JSemester semester = new JSemester();
    semester.setId(semesterId);
    semester.setAcademicYearId(UUID.randomUUID());

    JAcademicYear academicYear = new JAcademicYear();
    academicYear.setId(semester.getAcademicYearId());

    JTranscript transcriptEntity =
        JTranscript.builder()
            .id(UUID.randomUUID())
            .studentId(studentId)
            .semesterId(semesterId)
            .status("PENDING")
            .build();

    Transcript transcriptDto =
        new Transcript(transcriptEntity.getId(), studentId, semesterId, "PENDING", null, null);

    when(transcriptRepository.save(any(JTranscript.class))).thenReturn(transcriptEntity);

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

    when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));

    when(academicYearRepository.findById(semester.getAcademicYearId()))
        .thenReturn(Optional.of(academicYear));

    when(transcriptPdfGenerator.generate(any(), any(), any(), any(), any()))
        .thenReturn(File.createTempFile("test", ".pdf"));

    when(transcriptMapper.toDto(any())).thenReturn(transcriptDto);

    Transcript result =
        transcriptService.requestTranscript(studentId, semesterId, requesterId, false);

    assertNotNull(result);
  }
}
