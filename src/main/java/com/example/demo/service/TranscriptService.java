package com.example.demo.service;

import com.example.demo.endpoint.event.EventProducer;
<<<<<<< HEAD
import com.example.demo.endpoint.event.model.SendEmailRequested;
=======
import com.example.demo.endpoint.event.model.TranscriptRequestedEvent;
>>>>>>> 5d0c5b6 (chore(service): business services - update application business logic and event services)
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * Main-app side of the transcript flow. Generation itself (PDFBox + S3 upload) happens
 * asynchronously in the worker app, in {@code TranscriptRequestedEventService}, following the Poja
 * "asynchronous reply by email" pattern: the request thread only saves a PENDING row and produces
 * an event — it never blocks on PDF generation or S3 I/O.
 */
@Service
@AllArgsConstructor
public class TranscriptService {

  private final TranscriptRepository transcriptRepository;
  private final TranscriptValidator transcriptValidator;
  private final TranscriptMapper transcriptMapper;
<<<<<<< HEAD
  private final TranscriptPdfGenerator transcriptPdfGenerator;
  private final BucketComponent bucketComponent;
  private final EventProducer<SendEmailRequested> eventProducer;
  private final StudentRepository studentRepository;
  private final SemesterRepository semesterRepository;
  private final AcademicYearRepository academicYearRepository;
=======
  private final EventProducer<TranscriptRequestedEvent> eventProducer;
>>>>>>> 5d0c5b6 (chore(service): business services - update application business logic and event services)

  @SneakyThrows
  public Transcript requestTranscript(
      UUID studentId, UUID semesterId, UUID requesterId, boolean requesterIsAdmin) {
    transcriptValidator.validateRequesterCanAccess(requesterId, requesterIsAdmin, studentId);

    var entity =
        JTranscript.builder().studentId(studentId).semesterId(semesterId).status("PENDING").build();
    var savedEntity = transcriptRepository.save(entity);

<<<<<<< HEAD
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

    var semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semesterId));

    var academicYear = academicYearRepository.findById(semester.getAcademicYearId()).orElse(null);

    File pdfFile =
        transcriptPdfGenerator.generate(
            student, semester, academicYear, List.of(), BigDecimal.ZERO);
    String bucketKey = "transcripts/" + studentId + "/" + semesterId + ".pdf";
    bucketComponent.upload(pdfFile, bucketKey);

    var emailEvent =
        SendEmailRequested.builder()
            .to(student.getEmail())
            .subject("Official Transcript")
            .htmlBody("<p>Hello, please find attached your official transcript.</p>")
            .attachmentBucketKey(bucketKey)
            .build();

    eventProducer.accept(List.of(emailEvent));
=======
    var event =
        TranscriptRequestedEvent.builder()
            .transcriptId(savedEntity.getId())
            .studentId(studentId)
            .semesterId(semesterId)
            .build();
    eventProducer.accept(List.of(event));
>>>>>>> 5d0c5b6 (chore(service): business services - update application business logic and event services)

    return transcriptMapper.toDto(savedEntity);
  }

  public List<Transcript> getTranscriptsForStudent(UUID studentId) {
    return transcriptRepository.findByStudentId(studentId).stream()
        .map(transcriptMapper::toDto)
        .toList();
  }

  public Transcript markGenerated(UUID transcriptId, String s3Url) {
    var entity =
        transcriptRepository
            .findById(transcriptId)
            .orElseThrow(
                () -> new IllegalArgumentException("Transcript not found: " + transcriptId));
    entity.setS3Url(s3Url);
    entity.setStatus("GENERATED");
    entity.setGeneratedAt(LocalDateTime.now());
    return transcriptMapper.toDto(transcriptRepository.save(entity));
  }
}
