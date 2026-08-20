package com.example.demo.endpoint.web.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeViewController {

  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("title", "Bienvenue sur Notehei");
    model.addAttribute("message", "Gestion des notes étudiantes");
    return "index";
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("title", "Dashboard");
    model.addAttribute("user", "Utilisateur");
    return "dashboard";
  }

  @GetMapping("/transcripts")
  public String transcripts(Model model) {
    model.addAttribute("title", "Relevés de notes");
    return "transcripts";
  }

  @GetMapping("/grades")
  public String grades(Model model) {
    model.addAttribute("title", "Notes");
    return "grades";
  }
}
