package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JCourse;
import com.example.demo.entity.JGroup;
import com.example.demo.entity.JTeacher;
import com.example.demo.entity.JTeachingAssignment;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeachingAssignmentRepositoryIT extends FacadeIT {

  @Autowired private TeachingAssignmentRepository teachingAssignmentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private CohortRepository cohortRepository;

  private JTeacher teacher;
  private JCourse course;
  private JGroup group;

  @BeforeEach
  void setUp() {
    teachingAssignmentRepository.deleteAll();
    teacherRepository.deleteAll();
    courseRepository.deleteAll();
    groupRepository.deleteAll();
    cohortRepository.deleteAll();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2037).build());
    group =
        groupRepository.save(JGroup.builder().reference("TA1").cohortId(cohort.getId()).build());
    course =
        courseRepository.save(
            JCourse.builder()
                .reference("TA-COURSE-" + UUID.randomUUID().toString().substring(0, 8))
                .title("Teaching assignment test course")
                .coefficient(new BigDecimal("1.00"))
                .build());
    teacher =
        teacherRepository.save(
            JTeacher.builder()
                .firstName("Tiana")
                .lastName("Rakoto")
                .email("ta-repo-it@notehei.local")
                .password("secret")
                .address("Antananarivo")
                .matricule("TCH90001")
                .build());
  }

  @Test
  void existsByTeacherIdAndCourseIdAndGroupId_reflects_saved_state() {
    assertThat(
            teachingAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupId(
                teacher.getId(), course.getId(), group.getId()))
        .isFalse();

    teachingAssignmentRepository.save(
        JTeachingAssignment.builder()
            .teacherId(teacher.getId())
            .courseId(course.getId())
            .groupId(group.getId())
            .build());

    assertThat(
            teachingAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupId(
                teacher.getId(), course.getId(), group.getId()))
        .isTrue();
  }

  @Test
  void findByTeacherId_returns_all_assignments_for_that_teacher() {
    teachingAssignmentRepository.save(
        JTeachingAssignment.builder()
            .teacherId(teacher.getId())
            .courseId(course.getId())
            .groupId(group.getId())
            .build());

    var found = teachingAssignmentRepository.findByTeacherId(teacher.getId());

    assertThat(found).hasSize(1);
    assertThat(found.get(0).getCourseId()).isEqualTo(course.getId());
  }
}
