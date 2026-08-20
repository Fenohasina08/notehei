package com.example.demo.service.event;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.endpoint.event.model.TranscriptRequested;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.pdf.HtmlToPdfConverter;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.TranscriptPdfService;
import com.example.demo.service.TranscriptService;
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

  private final EventProducer<SendEmailRequested> emailEventProducer;

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

    var student =
        studentRepository
            .findById(event.getStudentId())
            .orElseThrow(
                () -> new IllegalArgumentException("Student not found: " + event.getStudentId()));

    var emailEvent =
        SendEmailRequested.builder()
            .to(student.getEmail())
            .subject("Votre relevé de notes")
            .htmlBody(
                """
                <html>
                  <body>
                    <p>Bonjour %s,</p>

                    <p>
                      Votre relevé de notes est maintenant disponible.
                    </p>

                    <p>
                      Vous trouverez votre relevé de notes
                      en pièce jointe de cet email.
                    </p>

                    <p>
                      Cordialement,<br>
                      L'équipe Note HEI
                    </p>
                  </body>
                </html>
                """
                    .formatted(student.getFirstName()))
            .attachmentBucketKey(bucketKey)
            .build();

    emailEventProducer.accept(List.of(emailEvent));

    log.info("Transcript generated and email requested for student {}", event.getStudentId());
  }
}
