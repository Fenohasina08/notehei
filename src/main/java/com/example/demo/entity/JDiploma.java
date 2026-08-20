package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "diploma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JDiploma {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "student_id", nullable = false, unique = true)
  private UUID studentId;

  @Column(name = "issue_date", nullable = false)
  private LocalDate issueDate;

  @Column(nullable = false, length = 30)
  private String status;
}
