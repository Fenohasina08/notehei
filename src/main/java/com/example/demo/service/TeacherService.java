package com.example.demo.service;

import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.entity.JTeacher;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.repository.TeacherRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private final TeacherRepository teacherRepository;
  private final MatriculeGenerator matriculeGenerator;

  public TeacherResponseDTO create(CreateTeacherDTO dto) {

    if (teacherRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    String matricule = matriculeGenerator.generateTeacherMatricule();

    JTeacher teacher =
        JTeacher.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(dto.getPassword())
            .birthdate(dto.getBirthdate())
            .address(dto.getAddress())
            .matricule(matricule)
            .build();

    JTeacher savedTeacher = teacherRepository.save(teacher);

    return TeacherMapper.toResponseDTO(savedTeacher);
  }

  public TeacherResponseDTO findById(UUID id) {
    JTeacher teacher =
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

    return TeacherMapper.toResponseDTO(teacher);
  }

  public TeacherResponseDTO findByEmail(String email) {
    JTeacher teacher =
        teacherRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

    return TeacherMapper.toResponseDTO(teacher);
  }
}
