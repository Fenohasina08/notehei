package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transcript")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JTranscript {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "semester_id", nullable = false)
  private UUID semesterId;

  @Column(name = "s3_url", length = 500)
  private String s3Url;

  @Column(name = "status", nullable = false, length = 30)
  private String status;

  @Column(name = "generated_at")
  private LocalDateTime generatedAt;
}
