package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

  private UUID id;

  private String firstName;

  private String lastName;

  private String email;

  private LocalDate birthdate;

  private String address;

  private String matricule;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
