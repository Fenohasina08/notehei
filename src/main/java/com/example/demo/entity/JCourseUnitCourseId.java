package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class JCourseUnitCourseId implements Serializable {

  private UUID courseUnitId;
  private UUID courseId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JCourseUnitCourseId that)) return false;
    return Objects.equals(courseUnitId, that.courseUnitId)
        && Objects.equals(courseId, that.courseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseUnitId, courseId);
  }
}
