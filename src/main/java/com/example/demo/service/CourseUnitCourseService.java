package com.example.demo.service;

import com.example.demo.entity.JCourseUnitCourse;
import com.example.demo.entity.JCourseUnitCourseId;
import com.example.demo.repository.CourseUnitCourseRepository;
import com.example.demo.validator.CourseUnitCourseValidator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseUnitCourseService {

  private final CourseUnitCourseRepository courseUnitCourseRepository;
  private final CourseUnitCourseValidator courseUnitCourseValidator;

  public void attachCourse(UUID courseUnitId, UUID courseId, int credits) {
    int currentTotal =
        courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId)).stream()
            .mapToInt(link -> link.getCredits() == null ? 0 : link.getCredits())
            .sum();
    courseUnitCourseValidator.validateDoesNotExceedCourseUnitCredits(
        courseUnitId, currentTotal + credits);

    courseUnitCourseRepository.save(new JCourseUnitCourse(courseUnitId, courseId, credits));
  }

  public void detachCourse(UUID courseUnitId, UUID courseId) {
    courseUnitCourseRepository.deleteById(new JCourseUnitCourseId(courseUnitId, courseId));
  }

  public List<JCourseUnitCourse> getCoursesForUnit(UUID courseUnitId) {
    return courseUnitCourseRepository.findByCourseUnitIdIn(List.of(courseUnitId));
  }

  public void validateComplete(UUID courseUnitId) {
    courseUnitCourseValidator.validateCreditsMatchCourseUnit(courseUnitId);
  }
}
