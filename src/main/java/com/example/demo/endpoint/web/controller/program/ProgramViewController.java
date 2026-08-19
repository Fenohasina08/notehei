package com.example.demo.endpoint.web.controller.program;

import com.example.demo.model.Program;
import com.example.demo.service.ProgramService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class ProgramViewController {

  private final ProgramService programService;

  @GetMapping("/screens/programs")
  public String list(Model model) {
    model.addAttribute("programs", programService.getAll());
    return "program/list";
  }

  @PostMapping("/screens/programs")
  public String create(@RequestParam String code, @RequestParam String name) {
    programService.create(new Program(null, code, name));
    return "redirect:/screens/programs";
  }
}
