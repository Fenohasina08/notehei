package com.example.demo.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Exposes a single {@link AuthenticationManager} backed by three {@link DaoAuthenticationProvider}s
 * (one per account type). Used by the Thymeleaf form login (see SecurityConfig's web filter chain):
 * the manager tries student, then teacher, then admin, and succeeds as soon as one provider matches
 * the email/password pair.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig {

  private final StudentUserDetailsService studentUserDetailsService;
  private final TeacherUserDetailsService teacherUserDetailsService;
  private final AdminUserDetailsService adminUserDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Bean
  public AuthenticationManager authenticationManager() {
    var studentProvider = new DaoAuthenticationProvider();
    studentProvider.setUserDetailsService(studentUserDetailsService);
    studentProvider.setPasswordEncoder(passwordEncoder);

    var teacherProvider = new DaoAuthenticationProvider();
    teacherProvider.setUserDetailsService(teacherUserDetailsService);
    teacherProvider.setPasswordEncoder(passwordEncoder);

    var adminProvider = new DaoAuthenticationProvider();
    adminProvider.setUserDetailsService(adminUserDetailsService);
    adminProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(List.of(studentProvider, teacherProvider, adminProvider));
  }
}
