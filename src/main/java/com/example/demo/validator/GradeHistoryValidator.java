package com.example.demo.validator;

import com.example.demo.exception.GradeValidationException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class GradeHistoryValidator {

  public void validateExactlyOneAuthor(UUID teacherId, UUID adminId) {
    var teacherSet = teacherId != null;
    var adminSet = adminId != null;
    if (teacherSet == adminSet) {
      throw new GradeValidationException(
          "A grade history entry must have exactly one author: either a teacher or an admin");
    }
  }
}
