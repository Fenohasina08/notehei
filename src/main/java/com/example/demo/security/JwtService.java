package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtService(
      @Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {

    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    this.expirationMs = expirationMs;
  }

  public String generateToken(UUID userId, String email, Role role) {

    Date now = new Date();

    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .subject(email)
        .claim("id", userId.toString())
        .claim("role", role.name())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(signingKey)
        .compact();
  }

  public Claims extractClaims(String token) {

    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  public boolean isTokenValid(String token) {

    try {
      extractClaims(token);
      return true;

    } catch (Exception e) {
      return false;
    }
  }

  public String extractEmail(String token) {

    return extractClaims(token).getSubject();
  }

  public Role extractRole(String token) {

    String role = extractClaims(token).get("role", String.class);

    return Role.valueOf(role);
  }

  public UUID extractUserId(String token) {

    String id = extractClaims(token).get("id", String.class);

    return UUID.fromString(id);
  }
}
