package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_unit_course")
@IdClass(JCourseUnitCourseId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JCourseUnitCourse {

  @Id
  @Column(name = "course_unit_id")
  private UUID courseUnitId;

  @Id
  @Column(name = "course_id")
  private UUID courseId;

  @Column(nullable = false)
  private Integer credits;
}
