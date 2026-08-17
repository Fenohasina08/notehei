package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

  @NotBlank private String oldPassword;

  @NotBlank
  @Size(min = 8, max = 100)
  private String newPassword;
}
