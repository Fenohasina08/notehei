package com.example.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    SecurityUser user = (SecurityUser) authentication.getPrincipal();

    switch (user.getRole()) {
      case STUDENT -> response.sendRedirect("/dashboard");
      case TEACHER -> response.sendRedirect("/dashboard");
      case ADMIN -> response.sendRedirect("/dashboard");
    }
  }
}
