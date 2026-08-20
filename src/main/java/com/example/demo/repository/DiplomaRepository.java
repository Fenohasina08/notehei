package com.example.demo.repository;

import com.example.demo.entity.JDiploma;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiplomaRepository extends JpaRepository<JDiploma, UUID> {
  Optional<JDiploma> findByStudentId(UUID studentId);
}
