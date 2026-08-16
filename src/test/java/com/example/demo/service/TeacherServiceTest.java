package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.entity.JTeacher;
import com.example.demo.repository.TeacherRepository;
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
class TeacherServiceTest {

  @Mock private TeacherRepository teacherRepository;

  @Mock private MatriculeGenerator matriculeGenerator;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private TeacherService teacherService;

  private JTeacher teacher;
  private CreateTeacherDTO createTeacherDTO;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    Timestamp now = Timestamp.valueOf("2026-08-16 10:00:00");

    createTeacherDTO =
        CreateTeacherDTO.builder()
            .firstName("Jane")
            .lastName("Smith")
            .email("jane.smith@test.com")
            .password("password123")
            .birthdate(LocalDate.of(1990, 5, 10))
            .address("Antananarivo")
            .build();

    teacher =
        JTeacher.builder()
            .id(id)
            .firstName("Jane")
            .lastName("Smith")
            .email("jane.smith@test.com")
            .password("$2a$10$hashedPassword")
            .birthdate(LocalDate.of(1990, 5, 10))
            .address("Antananarivo")
            .matricule("TCH26183")
            .createdAt(now)
            .updatedAt(now)
            .build();
  }

  @Test
  void create_shouldCreateTeacherSuccessfully() {
    when(teacherRepository.existsByEmail("jane.smith@test.com")).thenReturn(false);

    when(matriculeGenerator.generateTeacherMatricule()).thenReturn("TCH26183");

    when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");

    when(teacherRepository.save(any(JTeacher.class))).thenReturn(teacher);

    TeacherResponseDTO result = teacherService.create(createTeacherDTO);

    assertNotNull(result);
    assertEquals("Jane", result.getFirstName());
    assertEquals("Smith", result.getLastName());
    assertEquals("jane.smith@test.com", result.getEmail());
    assertEquals("TCH26183", result.getMatricule());

    verify(teacherRepository).existsByEmail("jane.smith@test.com");
    verify(matriculeGenerator).generateTeacherMatricule();
    verify(passwordEncoder).encode("password123");
    verify(teacherRepository).save(any(JTeacher.class));
  }

  @Test
  void create_shouldEncodePasswordBeforeSaving() {
    when(teacherRepository.existsByEmail(anyString())).thenReturn(false);

    when(matriculeGenerator.generateTeacherMatricule()).thenReturn("TCH26183");

    when(passwordEncoder.encode("password123")).thenReturn("ENCODED_PASSWORD");

    when(teacherRepository.save(any(JTeacher.class))).thenReturn(teacher);

    teacherService.create(createTeacherDTO);

    ArgumentCaptor<JTeacher> captor = ArgumentCaptor.forClass(JTeacher.class);

    verify(teacherRepository).save(captor.capture());

    JTeacher savedTeacher = captor.getValue();

    assertEquals("ENCODED_PASSWORD", savedTeacher.getPassword());
    assertEquals("TCH26183", savedTeacher.getMatricule());
  }

  @Test
  void create_shouldThrowExceptionWhenEmailAlreadyExists() {
    when(teacherRepository.existsByEmail("jane.smith@test.com")).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> teacherService.create(createTeacherDTO));

    assertEquals("Email already exists", exception.getMessage());

    verify(teacherRepository).existsByEmail("jane.smith@test.com");
    verify(teacherRepository, never()).save(any());
    verify(matriculeGenerator, never()).generateTeacherMatricule();
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  void findById_shouldReturnTeacherWhenFound() {
    UUID id = teacher.getId();

    when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));

    TeacherResponseDTO result = teacherService.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    assertEquals("Jane", result.getFirstName());
    assertEquals("TCH26183", result.getMatricule());

    verify(teacherRepository).findById(id);
  }

  @Test
  void findById_shouldThrowExceptionWhenTeacherNotFound() {
    UUID id = UUID.randomUUID();

    when(teacherRepository.findById(id)).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> teacherService.findById(id));

    assertEquals("Teacher not found", exception.getMessage());
  }

  @Test
  void findByEmail_shouldReturnTeacherWhenFound() {
    when(teacherRepository.findByEmail("jane.smith@test.com")).thenReturn(Optional.of(teacher));

    TeacherResponseDTO result = teacherService.findByEmail("jane.smith@test.com");

    assertNotNull(result);
    assertEquals("jane.smith@test.com", result.getEmail());
    assertEquals("TCH26183", result.getMatricule());

    verify(teacherRepository).findByEmail("jane.smith@test.com");
  }

  @Test
  void findByEmail_shouldThrowExceptionWhenTeacherNotFound() {
    when(teacherRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> teacherService.findByEmail("unknown@test.com"));

    assertEquals("Teacher not found", exception.getMessage());
  }
}
