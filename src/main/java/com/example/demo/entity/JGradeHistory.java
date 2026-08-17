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
@Table(name = "grade_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JGradeHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "grade_id", nullable = false)
  private UUID gradeId;

  @Column(name = "old_value", nullable = false, precision = 4, scale = 2)
  private BigDecimal oldValue;

  @Column(name = "new_value", nullable = false, precision = 4, scale = 2)
  private BigDecimal newValue;

  @Column(name = "reason")
  private String reason;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Column(name = "teacher_matricule")
  private String teacherMatricule;

  @Column(name = "admin_id")
  private UUID adminId;
}
