package com.example.demo.repository;

import com.example.demo.entity.JProgram;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<JProgram, UUID> {

  boolean existsByCode(String code);
}
