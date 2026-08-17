package com.example.demo.dto;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
  private String token;
  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
  private String role;
}
