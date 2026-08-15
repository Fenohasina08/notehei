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
public class AdminResponseDTO {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDate birthdate;
  private String address;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
