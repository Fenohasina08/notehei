package com.example.demo.service;

import com.example.demo.entity.JDiploma;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JTranscript;
import com.example.demo.repository.DiplomaRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.validator.DiplomaValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DiplomaService {
  private final DiplomaRepository diplomaRepository;
  private final TranscriptRepository transcriptRepository;
  private final SemesterRepository semesterRepository;
  private final DiplomaValidator diplomaValidator;

  @Transactional
  public JDiploma evaluateAndAwardDiploma(UUID studentId) {
    var existingDiploma = diplomaRepository.findByStudentId(studentId);
    if (existingDiploma.isPresent()) {
      return existingDiploma.get();
    }

    List<JTranscript> transcripts = transcriptRepository.findByStudentId(studentId);

    Set<UUID> semesterIds =
        transcripts.stream()
            .filter(t -> "VALIDATED".equalsIgnoreCase(t.getStatus()))
            .map(JTranscript::getSemesterId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    Set<Integer> validatedSemesterNumbers =
        semesterRepository.findAllById(semesterIds).stream()
            .map(JSemester::getNumber)
            .collect(Collectors.toSet());

    diplomaValidator.validateSemesters(validatedSemesterNumbers);

    JDiploma diploma =
        JDiploma.builder()
            .id(UUID.randomUUID())
            .studentId(studentId)
            .issueDate(LocalDate.now())
            .status("VALIDATED")
            .build();

    return diplomaRepository.save(diploma);
  }
}
