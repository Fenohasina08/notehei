package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.JCourse;
import com.example.demo.mapper.CourseMapper;
import com.example.demo.model.Course;
import com.example.demo.repository.CourseRepository;
import com.example.demo.validator.CourseValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock private CourseRepository courseRepository;

  @Mock private CourseValidator courseValidator;

  @Mock private CourseMapper courseMapper;

  @InjectMocks private CourseService courseService;

  private UUID courseId;
  private Course course;
  private JCourse jCourse;

  @BeforeEach
  void setUp() {
    courseId = UUID.randomUUID();

    course = new Course(courseId, "JAVA101", "Java Programming", new BigDecimal("2.00"));

    jCourse =
        JCourse.builder()
            .id(courseId)
            .reference("JAVA101")
            .title("Java Programming")
            .coefficient(new BigDecimal("2.00"))
            .build();
  }

  @Test
  void createCourse_shouldCreateCourseSuccessfully() {
    when(courseMapper.toEntity(course)).thenReturn(jCourse);
    when(courseRepository.save(jCourse)).thenReturn(jCourse);
    when(courseMapper.toDto(jCourse)).thenReturn(course);

    Course result = courseService.createCourse(course);

    assertNotNull(result);
    assertEquals(courseId, result.id());
    assertEquals("JAVA101", result.reference());
    assertEquals("Java Programming", result.title());
    assertEquals(new BigDecimal("2.00"), result.coefficient());

    verify(courseMapper).toEntity(course);
    verify(courseValidator).validate(jCourse);
    verify(courseRepository).save(jCourse);
    verify(courseMapper).toDto(jCourse);
  }

  @Test
  void createCourse_shouldValidateCourseBeforeSaving() {
    when(courseMapper.toEntity(course)).thenReturn(jCourse);
    when(courseRepository.save(jCourse)).thenReturn(jCourse);
    when(courseMapper.toDto(jCourse)).thenReturn(course);

    courseService.createCourse(course);

    var inOrder = inOrder(courseMapper, courseValidator, courseRepository);

    inOrder.verify(courseMapper).toEntity(course);
    inOrder.verify(courseValidator).validate(jCourse);
    inOrder.verify(courseRepository).save(jCourse);
  }

  @Test
  void createCourse_shouldNotSaveWhenValidationFails() {
    when(courseMapper.toEntity(course)).thenReturn(jCourse);

    doThrow(new RuntimeException("Invalid course")).when(courseValidator).validate(jCourse);

    assertThrows(RuntimeException.class, () -> courseService.createCourse(course));

    verify(courseMapper).toEntity(course);
    verify(courseValidator).validate(jCourse);
    verify(courseRepository, never()).save(any(JCourse.class));
    verify(courseMapper, never()).toDto(any(JCourse.class));
  }

  @Test
  void getAllCourses_shouldReturnAllCourses() {
    JCourse secondJCourse =
        JCourse.builder()
            .id(UUID.randomUUID())
            .reference("SQL101")
            .title("Database")
            .coefficient(new BigDecimal("3.00"))
            .build();

    Course secondCourse =
        new Course(secondJCourse.getId(), "SQL101", "Database", new BigDecimal("3.00"));

    when(courseRepository.findAll()).thenReturn(List.of(jCourse, secondJCourse));
    when(courseMapper.toDto(jCourse)).thenReturn(course);
    when(courseMapper.toDto(secondJCourse)).thenReturn(secondCourse);

    List<Course> result = courseService.getAllCourses();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("JAVA101", result.get(0).reference());
    assertEquals("SQL101", result.get(1).reference());

    verify(courseRepository).findAll();
    verify(courseMapper).toDto(jCourse);
    verify(courseMapper).toDto(secondJCourse);
  }

  @Test
  void getAllCourses_shouldReturnEmptyListWhenNoCourseExists() {
    when(courseRepository.findAll()).thenReturn(List.of());

    List<Course> result = courseService.getAllCourses();

    assertNotNull(result);
    assertEquals(0, result.size());

    verify(courseRepository).findAll();
    verifyNoInteractions(courseMapper);
  }

  @Test
  void getCourseById_shouldReturnCourseWhenFound() {
    when(courseRepository.getReferenceById(courseId)).thenReturn(jCourse);
    when(courseMapper.toDto(jCourse)).thenReturn(course);

    Course result = courseService.getCourseById(courseId);

    assertNotNull(result);
    assertEquals(courseId, result.id());
    assertEquals("JAVA101", result.reference());
    assertEquals("Java Programming", result.title());

    verify(courseRepository).getReferenceById(courseId);
    verify(courseMapper).toDto(jCourse);
  }

  @Test
  void deleteCourse_shouldDeleteCourseById() {
    courseService.deleteCourse(courseId);

    verify(courseRepository).deleteById(courseId);
  }
}
