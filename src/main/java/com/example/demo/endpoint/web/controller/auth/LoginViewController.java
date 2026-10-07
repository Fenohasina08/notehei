package com.example.demo.endpoint.web.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginViewController {

  @GetMapping("/login")
  public String login(@RequestParam(name = "error", required = false) String error, Model model) {

    if (error != null) {
      model.addAttribute("errorMessage", "Email ou mot de passe incorrect.");
    }

    return "auth/login";
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "auth/access-denied";
  }
}
