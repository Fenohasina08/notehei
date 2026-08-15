package com.example.demo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "teacher")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JTeacher {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  private LocalDate birthdate;

  @Column(nullable = false)
  private String address;

  @Column(name = "matricule", nullable = false, unique = true, length = 20, updatable = false)
  private String matricule;

  @Column(nullable = true)
  private Timestamp createdAt;

  @Column(nullable = true)
  private Timestamp updatedAt;
}
