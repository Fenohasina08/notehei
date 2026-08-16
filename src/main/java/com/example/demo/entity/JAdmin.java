package com.example.demo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JAdmin {
  @Id @GeneratedValue private UUID id;

  @Column(name = "firstname", nullable = false)
  private String firstName;

  @Column(name = "lastname", nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "birth_date")
  private LocalDate birthdate;

  @Column(nullable = false)
  private String address;

  @Column(nullable = true)
  private Timestamp createdAt;

  @Column(nullable = true)
  private Timestamp updatedAt;
}
