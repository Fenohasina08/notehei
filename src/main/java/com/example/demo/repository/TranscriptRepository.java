package com.example.demo.repository;

import com.example.demo.entity.JTranscript;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptRepository extends JpaRepository<JTranscript, UUID> {

  Optional<JTranscript> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);

  List<JTranscript> findByStudentId(UUID studentId);
}
