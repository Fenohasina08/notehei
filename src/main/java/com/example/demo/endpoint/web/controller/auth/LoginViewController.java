package com.example.demo.endpoint.web.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
