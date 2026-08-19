package com.example.demo.repository;

import com.example.demo.entity.JCourseUnitProgram;
import com.example.demo.entity.JCourseUnitProgramId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseUnitProgramRepository
    extends JpaRepository<JCourseUnitProgram, JCourseUnitProgramId> {

  List<JCourseUnitProgram> findByCourseUnitId(UUID courseUnitId);

  boolean existsByCourseUnitId(UUID courseUnitId);
}
