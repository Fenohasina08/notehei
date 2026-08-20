package com.example.demo.security;

import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentUserDetailsService implements UserDetailsService {

  private final StudentRepository studentRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var student =
        studentRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("No student found for email " + email));

    return new SecurityUser(
        student.getId(),
        student.getEmail(),
        student.getPassword(),
        student.getFirstName(),
        student.getLastName(),
        Role.STUDENT);
  }
}
