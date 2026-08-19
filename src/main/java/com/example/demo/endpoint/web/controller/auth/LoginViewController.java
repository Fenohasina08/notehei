package com.example.demo.endpoint.web.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Prepares the Model and returns view names only — no business logic. Actual authentication is
 * handled by Spring Security's form login (see SecurityConfig#webSecurityFilterChain), which posts
 * to the same {@code /login} URL.
 */
@Controller
public class LoginViewController {

  @GetMapping("/login")
  public String login(
      @RequestParam(name = "error", required = false) String error,
      @RequestParam(name = "logout", required = false) String logout,
      Model model) {
    if (error != null) {
      model.addAttribute("errorMessage", "Email ou mot de passe incorrect.");
    }
    if (logout != null) {
      model.addAttribute("logoutMessage", "Vous avez été déconnecté.");
    }
    return "login";
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "auth/access-denied";
  }
}
