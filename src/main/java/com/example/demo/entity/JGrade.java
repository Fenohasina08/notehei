package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JGrade {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "student_matricule", nullable = false, length = 20)
  private String studentMatricule;

  @Column(name = "exam_id", nullable = false)
  private UUID examId;

  @Column(name = "value", nullable = false, precision = 4, scale = 2)
  private BigDecimal value;

  @Column(name = "status", length = 30)
  private String status;

  @Column(name = "entered_at", nullable = false)
  private LocalDateTime enteredAt;

  @Column(name = "teacher_matricule", length = 20)
  private String teacherMatricule;

  @Column(name = "admin_id")
  private UUID adminId;
}
