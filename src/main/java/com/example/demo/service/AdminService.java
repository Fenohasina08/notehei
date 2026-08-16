package com.example.demo.service;

import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.dto.CreateAdminDTO;
import com.example.demo.entity.JAdmin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.repository.AdminRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final AdminRepository adminRepository;

  public AdminResponseDTO create(CreateAdminDTO dto) {

    if (adminRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    JAdmin admin =
        JAdmin.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(dto.getPassword())
            .birthdate(dto.getBirthdate())
            .address(dto.getAddress())
            .build();

    JAdmin savedAdmin = adminRepository.save(admin);

    return AdminMapper.toResponseDTO(savedAdmin);
  }

  public AdminResponseDTO findById(UUID id) {
    JAdmin admin =
        adminRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

    return AdminMapper.toResponseDTO(admin);
  }

  public AdminResponseDTO findByEmail(String email) {
    JAdmin admin =
        adminRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

    return AdminMapper.toResponseDTO(admin);
  }
}
