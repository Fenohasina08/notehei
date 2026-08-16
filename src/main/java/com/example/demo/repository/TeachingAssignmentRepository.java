package com.example.demo.repository;

import com.example.demo.entity.JTeachingAssignment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingAssignmentRepository extends JpaRepository<JTeachingAssignment, UUID> {

  boolean existsByTeacherIdAndCourseId(UUID teacherId, UUID courseId);
}
