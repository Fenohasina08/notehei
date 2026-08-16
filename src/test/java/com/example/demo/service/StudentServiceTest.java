package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.entity.JStudent;
import com.example.demo.repository.StudentRepository;
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
class StudentServiceTest {

  @Mock private StudentRepository studentRepository;

  @Mock private MatriculeGenerator matriculeGenerator;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private StudentService studentService;

  private JStudent student;
  private CreateStudentDTO createStudentDTO;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    Timestamp now = Timestamp.valueOf("2026-08-16 10:00:00");

    createStudentDTO =
        CreateStudentDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@test.com")
            .password("password123")
            .birthdate(LocalDate.of(2000, 1, 1))
            .address("Antananarivo")
            .build();

    student =
        JStudent.builder()
            .id(id)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@test.com")
            .password("$2a$10$hashedPassword")
            .birthdate(LocalDate.of(2000, 1, 1))
            .address("Antananarivo")
            .matricule("STD26182")
            .createdAt(now)
            .updatedAt(now)
            .build();
  }

  @Test
  void create_shouldCreateStudentSuccessfully() {
    when(studentRepository.existsByEmail(createStudentDTO.getEmail())).thenReturn(false);

    when(matriculeGenerator.generateStudentMatricule()).thenReturn("STD26182");

    when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");

    when(studentRepository.save(any(JStudent.class))).thenReturn(student);

    StudentResponseDTO result = studentService.create(createStudentDTO);

    assertNotNull(result);
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("john.doe@test.com", result.getEmail());
    assertEquals("STD26182", result.getMatricule());

    verify(studentRepository).existsByEmail("john.doe@test.com");
    verify(matriculeGenerator).generateStudentMatricule();
    verify(passwordEncoder).encode("password123");
    verify(studentRepository).save(any(JStudent.class));
  }

  @Test
  void create_shouldEncodePasswordBeforeSaving() {
    when(studentRepository.existsByEmail(anyString())).thenReturn(false);

    when(matriculeGenerator.generateStudentMatricule()).thenReturn("STD26182");

    when(passwordEncoder.encode("password123")).thenReturn("ENCODED_PASSWORD");

    when(studentRepository.save(any(JStudent.class))).thenReturn(student);

    studentService.create(createStudentDTO);

    ArgumentCaptor<JStudent> captor = ArgumentCaptor.forClass(JStudent.class);

    verify(studentRepository).save(captor.capture());

    JStudent savedStudent = captor.getValue();

    assertEquals("ENCODED_PASSWORD", savedStudent.getPassword());
    assertEquals("STD26182", savedStudent.getMatricule());
  }

  @Test
  void create_shouldThrowExceptionWhenEmailAlreadyExists() {
    when(studentRepository.existsByEmail("john.doe@test.com")).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> studentService.create(createStudentDTO));

    assertEquals("Email already exists", exception.getMessage());

    verify(studentRepository).existsByEmail("john.doe@test.com");
    verify(studentRepository, never()).save(any());
    verify(matriculeGenerator, never()).generateStudentMatricule();
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  void findById_shouldReturnStudentWhenFound() {
    UUID id = student.getId();

    when(studentRepository.findById(id)).thenReturn(Optional.of(student));

    StudentResponseDTO result = studentService.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    assertEquals("John", result.getFirstName());
    assertEquals("john.doe@test.com", result.getEmail());
    assertEquals("STD26182", result.getMatricule());

    verify(studentRepository).findById(id);
  }

  @Test
  void findById_shouldThrowExceptionWhenStudentNotFound() {
    UUID id = UUID.randomUUID();

    when(studentRepository.findById(id)).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> studentService.findById(id));

    assertEquals("Student not found", exception.getMessage());

    verify(studentRepository).findById(id);
  }

  @Test
  void findByEmail_shouldReturnStudentWhenFound() {
    when(studentRepository.findByEmail("john.doe@test.com")).thenReturn(Optional.of(student));

    StudentResponseDTO result = studentService.findByEmail("john.doe@test.com");

    assertNotNull(result);
    assertEquals("john.doe@test.com", result.getEmail());
    assertEquals("STD26182", result.getMatricule());

    verify(studentRepository).findByEmail("john.doe@test.com");
  }

  @Test
  void findByEmail_shouldThrowExceptionWhenStudentNotFound() {
    when(studentRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> studentService.findByEmail("unknown@test.com"));

    assertEquals("Student not found", exception.getMessage());

    verify(studentRepository).findByEmail("unknown@test.com");
  }
}
