package com.example.demo.repository;

import com.example.demo.entity.JStudent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<JStudent, UUID> {

  Optional<JStudent> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByMatricule(String matricule);
}
