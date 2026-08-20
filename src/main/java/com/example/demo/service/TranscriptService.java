package com.example.demo.service;

import com.example.demo.entity.JTranscript;
import com.example.demo.mapper.TranscriptMapper;
import com.example.demo.model.Transcript;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.validator.TranscriptValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TranscriptService {

  private final TranscriptRepository transcriptRepository;
  private final TranscriptValidator transcriptValidator;
  private final TranscriptMapper transcriptMapper;

  public Transcript requestTranscript(
      UUID studentId, UUID semesterId, UUID requesterId, boolean requesterIsAdmin) {

    transcriptValidator.validateRequesterCanAccess(requesterId, requesterIsAdmin, studentId);

    var entity =
        JTranscript.builder().studentId(studentId).semesterId(semesterId).status("PENDING").build();

    var savedEntity = transcriptRepository.save(entity);

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
