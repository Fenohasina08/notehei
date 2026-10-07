package com.example.demo.endpoint.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String COOKIE_NAME = "NOTEHEI_TOKEN";

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(
      @Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {

    LoginResponseDTO loginResponse = authService.login(dto);

    ResponseCookie cookie = buildTokenCookie(loginResponse.getToken(), 60 * 60 * 24, request);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {

    ResponseCookie cookie = buildTokenCookie("", 0, request);

    return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
  }

  private ResponseCookie buildTokenCookie(String value, long maxAge, HttpServletRequest request) {

    return ResponseCookie.from(COOKIE_NAME, value)
        .httpOnly(true)
        .secure(request.isSecure())
        .path("/")
        .maxAge(maxAge)
        .sameSite("Lax")
        .build();
  }
}
