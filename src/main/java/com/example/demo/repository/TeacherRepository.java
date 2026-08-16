package com.example.demo.repository;

import com.example.demo.entity.JTeacher;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<JTeacher, UUID> {

  Optional<JTeacher> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByMatricule(String matricule);
}
