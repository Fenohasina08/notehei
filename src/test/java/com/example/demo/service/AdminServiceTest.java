package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.dto.CreateAdminDTO;
import com.example.demo.entity.JAdmin;
import com.example.demo.repository.AdminRepository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  @Mock private AdminRepository adminRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AdminService adminService;

  private JAdmin admin;
  private CreateAdminDTO createAdminDTO;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    Timestamp now = Timestamp.valueOf("2026-08-16 10:00:00");

    createAdminDTO =
        CreateAdminDTO.builder()
            .firstName("Admin")
            .lastName("Test")
            .email("admin@test.com")
            .password("password123")
            .birthdate(LocalDate.of(1985, 1, 1))
            .address("Antananarivo")
            .build();

    admin =
        JAdmin.builder()
            .id(id)
            .firstName("Admin")
            .lastName("Test")
            .email("admin@test.com")
            .password("$2a$10$hashedPassword")
            .birthdate(LocalDate.of(1985, 1, 1))
            .address("Antananarivo")
            .createdAt(now)
            .updatedAt(now)
            .build();
  }

  @Test
  void create_shouldCreateAdminSuccessfully() {
    when(adminRepository.existsByEmail("admin@test.com")).thenReturn(false);

    when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");

    when(adminRepository.save(any(JAdmin.class))).thenReturn(admin);

    AdminResponseDTO result = adminService.create(createAdminDTO);

    assertNotNull(result);
    assertEquals("Admin", result.getFirstName());
    assertEquals("Test", result.getLastName());
    assertEquals("admin@test.com", result.getEmail());

    verify(adminRepository).existsByEmail("admin@test.com");
    verify(passwordEncoder).encode("password123");
    verify(adminRepository).save(any(JAdmin.class));
  }

  @Test
  void create_shouldEncodePasswordBeforeSaving() {
    when(adminRepository.existsByEmail(anyString())).thenReturn(false);

    when(passwordEncoder.encode("password123")).thenReturn("ENCODED_PASSWORD");

    when(adminRepository.save(any(JAdmin.class))).thenReturn(admin);

    adminService.create(createAdminDTO);

    ArgumentCaptor<JAdmin> captor = ArgumentCaptor.forClass(JAdmin.class);

    verify(adminRepository).save(captor.capture());

    JAdmin savedAdmin = captor.getValue();

    assertEquals("ENCODED_PASSWORD", savedAdmin.getPassword());
  }

  @Test
  void create_shouldThrowExceptionWhenEmailAlreadyExists() {
    when(adminRepository.existsByEmail("admin@test.com")).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> adminService.create(createAdminDTO));

    assertEquals("Email already exists", exception.getMessage());

    verify(adminRepository).existsByEmail("admin@test.com");
    verify(adminRepository, never()).save(any());
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  void findById_shouldReturnAdminWhenFound() {
    UUID id = admin.getId();

    when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

    AdminResponseDTO result = adminService.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    assertEquals("Admin", result.getFirstName());
    assertEquals("admin@test.com", result.getEmail());

    verify(adminRepository).findById(id);
  }

  @Test
  void findById_shouldThrowExceptionWhenAdminNotFound() {
    UUID id = UUID.randomUUID();

    when(adminRepository.findById(id)).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> adminService.findById(id));

    assertEquals("Admin not found", exception.getMessage());
  }

  @Test
  void findByEmail_shouldReturnAdminWhenFound() {
    when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

    AdminResponseDTO result = adminService.findByEmail("admin@test.com");

    assertNotNull(result);
    assertEquals("admin@test.com", result.getEmail());

    verify(adminRepository).findByEmail("admin@test.com");
  }

  @Test
  void findByEmail_shouldThrowExceptionWhenAdminNotFound() {
    when(adminRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> adminService.findByEmail("unknown@test.com"));

    assertEquals("Admin not found", exception.getMessage());
  }
}
