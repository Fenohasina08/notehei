package com.example.demo.repository;

import com.example.demo.entity.JCourseUnitCourse;
import com.example.demo.entity.JCourseUnitCourseId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseUnitCourseRepository
    extends JpaRepository<JCourseUnitCourse, JCourseUnitCourseId> {

  List<JCourseUnitCourse> findByCourseUnitIdIn(List<UUID> courseUnitIds);
}
