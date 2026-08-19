package com.example.demo.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.endpoint.event.model.TranscriptRequestedEvent;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.hash.FileHash;
import com.example.demo.file.hash.FileHashAlgorithm;
import com.example.demo.file.pdf.TranscriptPdfGenerator;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.service.TranscriptService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TranscriptRequestedEventServiceTest {

  @Mock private TranscriptRepository transcriptRepository;

  @Mock private TranscriptService transcriptService;

  @Mock private StudentRepository studentRepository;

  @Mock private SemesterRepository semesterRepository;

  @Mock private AcademicYearRepository academicYearRepository;

  @Mock private TranscriptPdfGenerator transcriptPdfGenerator;

  @Mock private BucketComponent bucketComponent;

  @Mock private EventProducer<SendEmailRequested> eventProducer;

  @InjectMocks private TranscriptRequestedEventService service;

  private UUID transcriptId;
  private UUID studentId;
  private UUID semesterId;
  private UUID academicYearId;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);

    transcriptId = UUID.randomUUID();
    studentId = UUID.randomUUID();
    semesterId = UUID.randomUUID();
    academicYearId = UUID.randomUUID();

    var student = new JStudent();
    student.setId(studentId);
    student.setEmail("student.transcript@notehei.local");

    var semester = new JSemester();
    semester.setId(semesterId);
    semester.setAcademicYearId(academicYearId);

    var academicYear = new JAcademicYear();
    academicYear.setId(academicYearId);

    when(transcriptRepository.existsById(transcriptId)).thenReturn(true);

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

    when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));

    when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));

    when(transcriptPdfGenerator.generate(any(), any(), any(), any(), any()))
        .thenReturn(File.createTempFile("transcript-test", ".pdf"));

    when(bucketComponent.upload(any(File.class), any(String.class)))
        .thenReturn(new FileHash(FileHashAlgorithm.SHA256, "fake-hash"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void generates_uploads_delegates_markGenerated_and_triggers_email() throws IOException {
    var event =
        TranscriptRequestedEvent.builder()
            .transcriptId(transcriptId)
            .studentId(studentId)
            .semesterId(semesterId)
            .build();

    service.accept(event);

    // PDF generated then uploaded
    verify(transcriptPdfGenerator).generate(any(), any(), any(), any(), any());

    verify(bucketComponent).upload(any(File.class), any(String.class));

    // Delegates the GENERATED transition to TranscriptService.markGenerated
    // (single source of truth for that state change),
    // instead of touching the repository directly.
    ArgumentCaptor<String> bucketKeyCaptor = ArgumentCaptor.forClass(String.class);

    verify(transcriptService).markGenerated(eq(transcriptId), bucketKeyCaptor.capture());

    assertNotNull(bucketKeyCaptor.getValue());

    assertEquals(
        "transcripts/" + studentId + "/" + semesterId + ".pdf", bucketKeyCaptor.getValue());

    // Email event produced for the student
    ArgumentCaptor<List<SendEmailRequested>> emailCaptor = ArgumentCaptor.forClass(List.class);

    verify(eventProducer).accept(emailCaptor.capture());

    SendEmailRequested emailEvent = emailCaptor.getValue().get(0);

    assertEquals("student.transcript@notehei.local", emailEvent.getTo());

    assertNotNull(emailEvent.getAttachmentBucketKey());
  }
}
