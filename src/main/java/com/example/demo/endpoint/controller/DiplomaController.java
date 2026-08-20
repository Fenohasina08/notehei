package com.example.demo.endpoint.controller;

import com.example.demo.entity.JDiploma;
import com.example.demo.service.DiplomaService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DiplomaController {
  private final DiplomaService diplomaService;

  @PostMapping("/students/{studentId}/diploma/evaluate")
  public JDiploma evaluateDiplomaForStudent(@PathVariable UUID studentId) {
    return diplomaService.evaluateAndAwardDiploma(studentId);
  }
}
