package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.TranscriptRequested;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.pdf.HtmlToPdfConverter;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.TranscriptPdfService;
import com.example.demo.service.TranscriptService;
import jakarta.mail.internet.InternetAddress;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class TranscriptRequestedService implements Consumer<TranscriptRequested> {

  private final TranscriptPdfService transcriptPdfService;
  private final HtmlToPdfConverter htmlToPdfConverter;
  private final BucketComponent bucketComponent;
  private final TranscriptService transcriptService;
  private final StudentRepository studentRepository;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(TranscriptRequested event) {
    log.info("Generating transcript: {}", event);

    var html =
        transcriptPdfService.buildTranscriptHtml(event.getStudentId(), event.getSemesterId());
    var pdfFile = htmlToPdfConverter.apply(html);

    var bucketKey =
        "transcripts/%s/%s-%d.pdf"
            .formatted(event.getStudentId(), event.getSemesterId(), Instant.now().toEpochMilli());
    bucketComponent.upload(pdfFile, bucketKey);

    transcriptService.markGenerated(event.getTranscriptId(), bucketKey);

    sendByEmail(event, pdfFile);
  }

  private void sendByEmail(TranscriptRequested event, java.io.File pdfFile) throws Exception {
    var student =
        studentRepository
            .findById(event.getStudentId())
            .orElseThrow(
                () -> new IllegalArgumentException("Student not found: " + event.getStudentId()));

    var email =
        new Email(
            new InternetAddress(student.getEmail()),
            List.of(),
            List.of(),
            "Votre relevé de notes",
            "<p>Bonjour "
                + student.getFirstName()
                + ", veuillez trouver ci-joint votre relevé de notes.</p>",
            List.of(pdfFile));
    mailer.accept(email);
  }
}
