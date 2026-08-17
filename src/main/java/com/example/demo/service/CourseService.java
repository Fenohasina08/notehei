package com.example.demo.service;

import com.example.demo.mapper.CourseMapper;
import com.example.demo.model.Course;
import com.example.demo.repository.CourseRepository;
import com.example.demo.validator.CourseValidator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;
  private final CourseValidator courseValidator;
  private final CourseMapper courseMapper;

  public Course createCourse(Course course) {
    var entity = courseMapper.toEntity(course);
    courseValidator.validate(entity);
    return courseMapper.toDto(courseRepository.save(entity));
  }

  public List<Course> getAllCourses() {
    return courseRepository.findAll().stream().map(courseMapper::toDto).toList();
  }

  public Course getCourseById(UUID courseId) {
    return courseMapper.toDto(courseRepository.getReferenceById(courseId));
  }

  public void deleteCourse(UUID courseId) {
    courseRepository.deleteById(courseId);
  }
}
