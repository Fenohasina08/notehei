package com.example.demo.repository;

import com.example.demo.entity.JAdmin;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<JAdmin, UUID> {

  Optional<JAdmin> findByEmail(String email);

  boolean existsByEmail(String email);
}
