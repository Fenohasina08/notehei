package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class JCourseUnitProgramId implements Serializable {

  private UUID courseUnitId;
  private UUID programId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JCourseUnitProgramId that)) return false;
    return Objects.equals(courseUnitId, that.courseUnitId)
        && Objects.equals(programId, that.programId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseUnitId, programId);
  }
}
