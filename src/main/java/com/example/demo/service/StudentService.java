package com.example.demo.service;

import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.entity.JStudent;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.repository.StudentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final MatriculeGenerator matriculeGenerator;

  public StudentResponseDTO create(CreateStudentDTO dto) {

    if (studentRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    String matricule = matriculeGenerator.generateStudentMatricule();

    JStudent student =
        JStudent.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(dto.getPassword())
            .birthdate(dto.getBirthdate())
            .address(dto.getAddress())
            .matricule(matricule)
            .build();

    JStudent savedStudent = studentRepository.save(student);

    return StudentMapper.toResponseDTO(savedStudent);
  }

  public StudentResponseDTO findById(UUID id) {
    JStudent student =
        studentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    return StudentMapper.toResponseDTO(student);
  }

  public StudentResponseDTO findByEmail(String email) {
    JStudent student =
        studentRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    return StudentMapper.toResponseDTO(student);
  }
}
