package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.entity.JTranscript;
import com.example.demo.mapper.TranscriptMapper;
import com.example.demo.model.Transcript;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.validator.TranscriptValidator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TranscriptServiceTest {

  @Mock private TranscriptRepository transcriptRepository;
  @Mock private TranscriptValidator transcriptValidator;
  @Mock private TranscriptMapper transcriptMapper;

  @InjectMocks private TranscriptService transcriptService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void requesting_a_transcript_saves_it_as_pending() {
    UUID studentId = UUID.randomUUID();
    UUID semesterId = UUID.randomUUID();
    UUID requesterId = studentId;

    JTranscript transcriptEntity =
        JTranscript.builder()
            .id(UUID.randomUUID())
            .studentId(studentId)
            .semesterId(semesterId)
            .status("PENDING")
            .build();

    Transcript transcriptDto =
        new Transcript(transcriptEntity.getId(), studentId, semesterId, null, "PENDING", null);

    when(transcriptRepository.save(any(JTranscript.class))).thenReturn(transcriptEntity);
    when(transcriptMapper.toDto(any())).thenReturn(transcriptDto);

    Transcript result =
        transcriptService.requestTranscript(studentId, semesterId, requesterId, false);

    assertNotNull(result);
    verify(transcriptValidator).validateRequesterCanAccess(requesterId, false, studentId);

    // The request thread never touches PDF generation or S3 — it only saves a PENDING row and
    // returns; the actual work happens asynchronously via the event produced by
    // TranscriptController.
    ArgumentCaptor<JTranscript> savedCaptor = ArgumentCaptor.forClass(JTranscript.class);
    verify(transcriptRepository).save(savedCaptor.capture());
    assertEquals("PENDING", savedCaptor.getValue().getStatus());
  }
}
