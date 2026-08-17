package com.example.demo.repository;

import com.example.demo.entity.JCourse;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<JCourse, UUID> {
  boolean existsByReference(String reference);
}
