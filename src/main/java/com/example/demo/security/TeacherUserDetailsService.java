package com.example.demo.security;

import com.example.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Loads a {@link SecurityUser} from the teacher account table. */
@Service
@RequiredArgsConstructor
public class TeacherUserDetailsService implements UserDetailsService {

  private final TeacherRepository teacherRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var teacher =
        teacherRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("No teacher found for email " + email));

    return new SecurityUser(
        teacher.getId(),
        teacher.getEmail(),
        teacher.getPassword(),
        teacher.getFirstName(),
        teacher.getLastName(),
        Role.TEACHER);
  }
}
