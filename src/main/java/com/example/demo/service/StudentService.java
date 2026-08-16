package com.example.demo.service;

import com.example.demo.dto.CreateStudentDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.entity.JStudent;
import com.example.demo.exception.EmailAlreadyUsedException;
import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.repository.StudentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final MatriculeGenerator matriculeGenerator;
  private final PasswordEncoder passwordEncoder;

  public StudentResponseDTO create(CreateStudentDTO dto) {

    if (studentRepository.existsByEmail(dto.getEmail())) {
      throw new EmailAlreadyUsedException(dto.getEmail());
    }

    String matricule = matriculeGenerator.generateStudentMatricule();

    String encodedPassword = passwordEncoder.encode(dto.getPassword());

    JStudent student =
        JStudent.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(encodedPassword)
            .birthdate(dto.getBirthdate())
            .address(dto.getAddress())
            .matricule(matricule)
            .build();

    JStudent savedStudent = studentRepository.save(student);

    return StudentMapper.toResponseDTO(savedStudent);
  }

  public StudentResponseDTO findById(UUID id) {
    JStudent student =
        studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));

    return StudentMapper.toResponseDTO(student);
  }

  public StudentResponseDTO findByEmail(String email) {
    JStudent student =
        studentRepository.findByEmail(email).orElseThrow(() -> new StudentNotFoundException(email));

    return StudentMapper.toResponseDTO(student);
  }
}
