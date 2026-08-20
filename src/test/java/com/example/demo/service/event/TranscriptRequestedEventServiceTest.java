package com.example.demo.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.event.model.TranscriptRequested;
import com.example.demo.entity.JStudent;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.pdf.HtmlToPdfConverter;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.TranscriptPdfService;
import com.example.demo.service.TranscriptService;
import java.io.File;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranscriptRequestedServiceTest {

  @Mock private TranscriptPdfService transcriptPdfService;
  @Mock private HtmlToPdfConverter htmlToPdfConverter;
  @Mock private BucketComponent bucketComponent;
  @Mock private TranscriptService transcriptService;
  @Mock private StudentRepository studentRepository;
  @Mock private Mailer mailer;

  private TranscriptRequestedService service;

  @BeforeEach
  void setUp() {
    service =
        new TranscriptRequestedService(
            transcriptPdfService,
            htmlToPdfConverter,
            bucketComponent,
            transcriptService,
            studentRepository,
            mailer);
  }

  @Test
  void accept_shouldRenderUploadMarkGeneratedAndEmailTheStudent() throws Exception {
    var transcriptId = UUID.randomUUID();
    var studentId = UUID.randomUUID();
    var semesterId = UUID.randomUUID();
    var event =
        TranscriptRequested.builder()
            .transcriptId(transcriptId)
            .studentId(studentId)
            .semesterId(semesterId)
            .build();

    var html = "<html><body>fake transcript</body></html>";
    var pdfFile = File.createTempFile("transcript-test-", ".pdf");
    pdfFile.deleteOnExit();

    var student =
        JStudent.builder()
            .id(studentId)
            .firstName("Rindra")
            .lastName("Rakoto")
            .email("rindra@test.com")
            .password("irrelevant")
            .matricule("STD26123456")
            .build();

    when(transcriptPdfService.buildTranscriptHtml(studentId, semesterId)).thenReturn(html);
    when(htmlToPdfConverter.apply(html)).thenReturn(pdfFile);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

    service.accept(event);

    verify(transcriptPdfService).buildTranscriptHtml(studentId, semesterId);
    verify(htmlToPdfConverter).apply(html);

    var bucketKeyCaptor = ArgumentCaptor.forClass(String.class);
    verify(bucketComponent).upload(eq(pdfFile), bucketKeyCaptor.capture());
    assertThat(bucketKeyCaptor.getValue())
        .startsWith("transcripts/" + studentId + "/" + semesterId + "-")
        .endsWith(".pdf");

    var markGeneratedKeyCaptor = ArgumentCaptor.forClass(String.class);
    verify(transcriptService).markGenerated(eq(transcriptId), markGeneratedKeyCaptor.capture());
    assertThat(markGeneratedKeyCaptor.getValue()).isEqualTo(bucketKeyCaptor.getValue());

    var emailCaptor = ArgumentCaptor.forClass(Email.class);
    verify(mailer).accept(emailCaptor.capture());
    var sentEmail = emailCaptor.getValue();
    assertThat(sentEmail.to().getAddress()).isEqualTo("rindra@test.com");
    assertThat(sentEmail.attachments()).containsExactly(pdfFile);
    assertThat(sentEmail.subject()).isNotBlank();
    assertThat(sentEmail.htmlBody()).contains("Rindra");
  }

  @Test
  void accept_shouldThrowWhenStudentNoLongerExists() {
    var event =
        TranscriptRequested.builder()
            .transcriptId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .semesterId(UUID.randomUUID())
            .build();

    when(transcriptPdfService.buildTranscriptHtml(any(), any())).thenReturn("<html></html>");
    when(htmlToPdfConverter.apply(anyString())).thenReturn(new File("dummy.pdf"));
    when(studentRepository.findById(event.getStudentId())).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> service.accept(event));
    verify(transcriptService).markGenerated(eq(event.getTranscriptId()), anyString());
  }
}
