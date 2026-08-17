package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.JExam;
import com.example.demo.entity.JExamType;
import java.math.BigDecimal;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class ExamRepositoryIT extends FacadeIT {

  @Autowired private ExamRepository examRepository;
  @Autowired private DataSource dataSource;

  @Test
  void saves_then_retrieves_exam_by_course_id() {
    var courseId = insertMinimalCourse();

    var exam =
        JExam.builder()
            .courseId(courseId)
            .type(JExamType.CONTINUOUS_ASSESSMENT)
            .weighting(new BigDecimal("40.00"))
            .build();
    var saved = examRepository.save(exam);

    var found = examRepository.findByCourseId(courseId);

    assertThat(found).hasSize(1);
    assertThat(found.get(0).getId()).isEqualTo(saved.getId());
    assertThat(found.get(0).getWeighting()).isEqualByComparingTo("40.00");
    assertThat(found.get(0).getType()).isEqualTo(JExamType.CONTINUOUS_ASSESSMENT);
  }

  @Test
  void deletes_exam_by_id() {
    var courseId = insertMinimalCourse();
    var saved =
        examRepository.save(
            JExam.builder()
                .courseId(courseId)
                .type(JExamType.FINAL_EXAM)
                .weighting(new BigDecimal("60.00"))
                .build());

    examRepository.deleteById(saved.getId());

    assertThat(examRepository.findById(saved.getId())).isEmpty();
  }

  private UUID insertMinimalCourse() {
    var jdbcTemplate = new JdbcTemplate(dataSource);
    var id = UUID.randomUUID();
    jdbcTemplate.update(
        "INSERT INTO course (id, reference, title, coefficient) VALUES (?, ?, ?, ?)",
        id,
        "IT-" + id.toString().substring(0, 8),
        "ExamRepositoryIT test course",
        new BigDecimal("1.00"));
    return id;
  }
}
