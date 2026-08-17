package com.example.demo.service;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AdminRepository adminRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public LoginResponseDTO login(LoginRequestDTO dto) {
    var admin = adminRepository.findByEmail(dto.getEmail());
    if (admin.isPresent()) {
      return authenticate(
          admin.get().getId(),
          admin.get().getEmail(),
          admin.get().getPassword(),
          admin.get().getFirstName(),
          admin.get().getLastName(),
          dto.getPassword(),
          Role.ADMIN);
    }

    var student = studentRepository.findByEmail(dto.getEmail());
    if (student.isPresent()) {
      return authenticate(
          student.get().getId(),
          student.get().getEmail(),
          student.get().getPassword(),
          student.get().getFirstName(),
          student.get().getLastName(),
          dto.getPassword(),
          Role.STUDENT);
    }

    var teacher = teacherRepository.findByEmail(dto.getEmail());
    if (teacher.isPresent()) {
      return authenticate(
          teacher.get().getId(),
          teacher.get().getEmail(),
          teacher.get().getPassword(),
          teacher.get().getFirstName(),
          teacher.get().getLastName(),
          dto.getPassword(),
          Role.TEACHER);
    }

    throw new InvalidCredentialsException();
  }

  private LoginResponseDTO authenticate(
      java.util.UUID id,
      String email,
      String encodedPassword,
      String firstName,
      String lastName,
      String rawPassword,
      Role role) {

    if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new InvalidCredentialsException();
    }

    String token = jwtService.generateToken(id, email, role);

    return LoginResponseDTO.builder()
        .token(token)
        .id(id)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .role(role.name())
        .build();
  }
}
