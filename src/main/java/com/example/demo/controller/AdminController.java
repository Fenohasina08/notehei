package com.example.demo.controller;

import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.dto.CreateAdminDTO;
import com.example.demo.service.AdminService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @PostMapping
  public ResponseEntity<AdminResponseDTO> create(@Valid @RequestBody CreateAdminDTO dto) {
    AdminResponseDTO created = adminService.create(dto);
    return ResponseEntity.created(URI.create("/admins/" + created.getId())).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AdminResponseDTO> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(adminService.findById(id));
  }

  @GetMapping(params = "email")
  public ResponseEntity<AdminResponseDTO> findByEmail(@RequestParam String email) {
    return ResponseEntity.ok(adminService.findByEmail(email));
  }
}
