package com.example.demo.security;

import com.example.demo.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Loads a {@link SecurityUser} from the admin account table. */
@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

  private final AdminRepository adminRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var admin =
        adminRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("No admin found for email " + email));

    return new SecurityUser(
        admin.getId(),
        admin.getEmail(),
        admin.getPassword(),
        admin.getFirstName(),
        admin.getLastName(),
        Role.ADMIN);
  }
}
