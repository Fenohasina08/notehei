package com.example.demo.service;

import com.example.demo.dto.ChangePasswordDTO;
import com.example.demo.dto.CreateTeacherDTO;
import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.entity.JTeacher;
import com.example.demo.exception.EmailAlreadyUsedException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.TeacherNotFoundException;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.repository.TeacherRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private final TeacherRepository teacherRepository;
  private final MatriculeGenerator matriculeGenerator;
  private final PasswordEncoder passwordEncoder;

  public TeacherResponseDTO create(CreateTeacherDTO dto) {

    if (teacherRepository.existsByEmail(dto.getEmail())) {
      throw new EmailAlreadyUsedException(dto.getEmail());
    }

    String matricule = matriculeGenerator.generateTeacherMatricule();

    String encodedPassword = passwordEncoder.encode(dto.getPassword());

    JTeacher teacher =
        JTeacher.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(encodedPassword)
            .birthdate(dto.getBirthdate())
            .address(dto.getAddress())
            .matricule(matricule)
            .build();

    JTeacher savedTeacher = teacherRepository.save(teacher);

    return TeacherMapper.toResponseDTO(savedTeacher);
  }

  public TeacherResponseDTO findById(UUID id) {
    JTeacher teacher =
        teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException(id));

    return TeacherMapper.toResponseDTO(teacher);
  }

  public TeacherResponseDTO findByEmail(String email) {
    JTeacher teacher =
        teacherRepository.findByEmail(email).orElseThrow(() -> new TeacherNotFoundException(email));

    return TeacherMapper.toResponseDTO(teacher);
  }

  public void changePassword(UUID id, ChangePasswordDTO dto) {
    JTeacher teacher =
        teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException(id));

    if (!passwordEncoder.matches(dto.getOldPassword(), teacher.getPassword())) {
      throw new InvalidCredentialsException();
    }

    teacher.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    teacherRepository.save(teacher);
  }
}
