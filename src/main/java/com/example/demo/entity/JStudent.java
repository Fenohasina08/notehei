package com.example.demo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JStudent {

  @Id @GeneratedValue private UUID id;

  @Column(name = "firstname", nullable = false)
  private String firstName;

  @Column(name = "lastname", nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "birth_date")
  private LocalDate birthdate;

  private String address;

  @Column(name = "matricule", nullable = false, unique = true, length = 20, updatable = false)
  private String matricule;

  private Timestamp createdAt;

  private Timestamp updatedAt;
}
