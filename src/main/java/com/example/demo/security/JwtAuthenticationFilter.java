package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String HEADER = "Authorization";
  private static final String PREFIX = "Bearer ";

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(HEADER);

    if (header != null && header.startsWith(PREFIX)) {
      String token = header.substring(PREFIX.length());

      if (jwtService.isTokenValid(token)) {
        String email = jwtService.extractEmail(token);
        Role role = jwtService.extractRole(token);
        var userId = jwtService.extractUserId(token);

        var principal = new AuthenticatedUser(userId, email, role);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
