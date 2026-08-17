package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JTranscript;
import com.example.demo.exception.TranscriptValidationException;
import com.example.demo.mapper.TranscriptMapper;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.validator.TranscriptValidator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceTest {

  @Mock private TranscriptRepository transcriptRepository;
  @Mock private TranscriptValidator transcriptValidator;

  private final TranscriptMapper transcriptMapper = new TranscriptMapper();

  private TranscriptService transcriptService;

  @BeforeEach
  void setUp() {
    transcriptService =
        new TranscriptService(transcriptRepository, transcriptValidator, transcriptMapper);
  }

  @Test
  void student_can_request_their_own_transcript() {
    var studentId = UUID.randomUUID();
    var semesterId = UUID.randomUUID();
    var saved =
        JTranscript.builder()
            .id(UUID.randomUUID())
            .studentId(studentId)
            .semesterId(semesterId)
            .status("PENDING")
            .build();
    when(transcriptRepository.save(any(JTranscript.class))).thenReturn(saved);

    var result = transcriptService.requestTranscript(studentId, semesterId, studentId, false);

    assertThat(result.status()).isEqualTo("PENDING");
    verify(transcriptValidator).validateRequesterCanAccess(studentId, false, studentId);
  }

  @Test
  void validator_rejection_prevents_creation() {
    var studentId = UUID.randomUUID();
    var otherStudentId = UUID.randomUUID();
    org.mockito.Mockito.doThrow(new TranscriptValidationException("refusé"))
        .when(transcriptValidator)
        .validateRequesterCanAccess(otherStudentId, false, studentId);

    assertThatThrownBy(
            () ->
                transcriptService.requestTranscript(
                    studentId, UUID.randomUUID(), otherStudentId, false))
        .isInstanceOf(TranscriptValidationException.class);

    org.mockito.Mockito.verifyNoInteractions(transcriptRepository);
  }

  @Test
  void marks_transcript_as_generated_with_s3_url() {
    var transcriptId = UUID.randomUUID();
    var existing = JTranscript.builder().id(transcriptId).status("PENDING").build();
    when(transcriptRepository.findById(transcriptId)).thenReturn(Optional.of(existing));
    when(transcriptRepository.save(any(JTranscript.class))).thenAnswer(inv -> inv.getArgument(0));

    var result = transcriptService.markGenerated(transcriptId, "s3://bucket/releve.pdf");

    assertThat(result.status()).isEqualTo("GENERATED");
    assertThat(result.s3Url()).isEqualTo("s3://bucket/releve.pdf");
    assertThat(result.generatedAt()).isNotNull();
  }
}
