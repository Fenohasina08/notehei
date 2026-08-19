package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JCohort;
import com.example.demo.entity.JCourseUnit;
import com.example.demo.entity.JCourseUnitProgram;
import com.example.demo.entity.JProgram;
import com.example.demo.entity.JSemester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CourseUnitProgramRepositoryIT extends FacadeIT {

  @Autowired private CourseUnitProgramRepository courseUnitProgramRepository;

  @Autowired private CourseUnitRepository courseUnitRepository;

  @Autowired private ProgramRepository programRepository;

  @Autowired private SemesterRepository semesterRepository;

  @Autowired private CohortRepository cohortRepository;

  @Autowired private AcademicYearRepository academicYearRepository;

  private JCourseUnit courseUnit;
  private JProgram program;

  @BeforeEach
  void setUp() {
    cleanDatabase();

    var cohort = cohortRepository.save(JCohort.builder().entryYear(2036).build());

    var academicYear =
        academicYearRepository.save(
            JAcademicYear.builder().name("CUP-2036").startYear(2036).endYear(2037).build());

    var semester =
        semesterRepository.save(
            JSemester.builder()
                .number(1)
                .cohortId(cohort.getId())
                .academicYearId(academicYear.getId())
                .build());

    courseUnit =
        courseUnitRepository.save(
            JCourseUnit.builder()
                .code("UE-CUP")
                .name("Course unit program test")
                .credits(6)
                .semesterId(semester.getId())
                .build());

    program = programRepository.save(JProgram.builder().code("CUP").name("Test").build());
  }

  @Test
  void attaches_and_lists_programs_for_a_course_unit() {
    courseUnitProgramRepository.save(new JCourseUnitProgram(courseUnit.getId(), program.getId()));

    var found = courseUnitProgramRepository.findByCourseUnitId(courseUnit.getId());

    assertThat(found).hasSize(1);
    assertThat(found.get(0).getProgramId()).isEqualTo(program.getId());
  }

  @Test
  void existsByCourseUnitId_reflects_saved_state() {
    assertThat(courseUnitProgramRepository.existsByCourseUnitId(courseUnit.getId())).isFalse();

    courseUnitProgramRepository.save(new JCourseUnitProgram(courseUnit.getId(), program.getId()));

    assertThat(courseUnitProgramRepository.existsByCourseUnitId(courseUnit.getId())).isTrue();
  }
}
