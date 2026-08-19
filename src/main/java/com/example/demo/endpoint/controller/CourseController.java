package com.example.demo.endpoint.controller;

import com.example.demo.model.Course;
import com.example.demo.service.CourseService;
import com.example.demo.service.CourseUnitCourseService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

  private final CourseService courseService;
  private final CourseUnitCourseService courseUnitCourseService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Course> create(@RequestBody Course course) {
    Course created = courseService.createCourse(course);
    return ResponseEntity.created(URI.create("/courses/" + created.id())).body(created);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<List<Course>> getAll() {
    return ResponseEntity.ok(courseService.getAllCourses());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<Course> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(courseService.getCourseById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    courseService.deleteCourse(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // --- attachment to a course unit (course_unit_course) -------------------------------------

  @PostMapping("/course-units/{courseUnitId}/attach")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> attachToCourseUnit(
      @PathVariable UUID courseUnitId, @RequestBody Map<String, Object> body) {
    UUID courseId = UUID.fromString((String) body.get("courseId"));
    int credits = ((Number) body.get("credits")).intValue();
    courseUnitCourseService.attachCourse(courseUnitId, courseId, credits);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/course-units/{courseUnitId}/attach/{courseId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> detachFromCourseUnit(
      @PathVariable UUID courseUnitId, @PathVariable UUID courseId) {
    courseUnitCourseService.detachCourse(courseUnitId, courseId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/course-units/{courseUnitId}/complete")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<Void> validateCourseUnitComplete(@PathVariable UUID courseUnitId) {
    courseUnitCourseService.validateComplete(courseUnitId);
    return ResponseEntity.ok().build();
  }
}
