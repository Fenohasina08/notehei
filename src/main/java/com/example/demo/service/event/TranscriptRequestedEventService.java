package com.example.demo.service.event;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.endpoint.event.model.TranscriptRequestedEvent;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.pdf.TranscriptPdfGenerator;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.service.TranscriptService;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Consumes {@link TranscriptRequestedEvent} in the worker (Poja event-driven app): generates the
 * PDF (PDFBox, via {@link TranscriptPdfGenerator}), uploads it to S3 (via {@link BucketComponent}),
 * marks the {@code transcript} row as GENERATED (delegating to {@link
 * TranscriptService#markGenerated}, the single place that owns that transition), then produces a
 * {@link SendEmailRequested} event so the student is notified once the file is ready. Invoked by
 * the generic {@code MailboxEventHandler}/{@code EventServiceInvoker} — no dedicated handler class
 * is needed, this service's name ({@code TranscriptRequestedEvent} + {@code Service}) is enough for
 * it to be located by reflection.
 */
@Service
@AllArgsConstructor
@Slf4j
public class TranscriptRequestedEventService implements Consumer<TranscriptRequestedEvent> {

  private final TranscriptRepository transcriptRepository;
  private final TranscriptService transcriptService;
  private final StudentRepository studentRepository;
  private final SemesterRepository semesterRepository;
  private final AcademicYearRepository academicYearRepository;
  private final TranscriptPdfGenerator transcriptPdfGenerator;
  private final BucketComponent bucketComponent;
  private final EventProducer<SendEmailRequested> eventProducer;

  @SneakyThrows
  @Override
  public void accept(TranscriptRequestedEvent event) {
    // Fail fast, before spending time on PDF generation, if the transcript row is somehow gone.
    if (!transcriptRepository.existsById(event.getTranscriptId())) {
      throw new IllegalStateException("Transcript not found: " + event.getTranscriptId());
    }

    JStudent student =
        studentRepository
            .findById(event.getStudentId())
            .orElseThrow(
                () -> new IllegalStateException("Student not found: " + event.getStudentId()));

    JSemester semester =
        semesterRepository
            .findById(event.getSemesterId())
            .orElseThrow(
                () -> new IllegalStateException("Semester not found: " + event.getSemesterId()));

    JAcademicYear academicYear =
        academicYearRepository.findById(semester.getAcademicYearId()).orElse(null);

    File pdfFile =
        transcriptPdfGenerator.generate(
            student, semester, academicYear, List.of(), BigDecimal.ZERO);

    String bucketKey = "transcripts/" + student.getId() + "/" + semester.getId() + ".pdf";
    bucketComponent.upload(pdfFile, bucketKey);

    transcriptService.markGenerated(event.getTranscriptId(), bucketKey);

    log.info("Transcript {} generated and uploaded to {}", event.getTranscriptId(), bucketKey);

    var emailEvent =
        SendEmailRequested.builder()
            .to(student.getEmail())
            .subject("Votre relevé de notes est disponible")
            .htmlBody("<p>Bonjour, veuillez trouver ci-joint votre relevé de notes officiel.</p>")
            .attachmentBucketKey(bucketKey)
            .build();

    eventProducer.accept(List.of(emailEvent));
  }
}
