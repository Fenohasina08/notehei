package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_unit_program")
@IdClass(JCourseUnitProgramId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JCourseUnitProgram {

  @Id
  @Column(name = "course_unit_id")
  private UUID courseUnitId;

  @Id
  @Column(name = "program_id")
  private UUID programId;
}
