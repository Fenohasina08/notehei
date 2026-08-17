package com.example.demo.validator;

import com.example.demo.exception.GradeValidationException;
import org.springframework.stereotype.Component;

@Component
public class GradeHistoryValidator {

  public void validateExactlyOneAuthor(String teacherMatricule, java.util.UUID adminId) {
    var teacherSet = teacherMatricule != null;
    var adminSet = adminId != null;

    if (teacherSet == adminSet) {
      throw new GradeValidationException(
          "A grade history entry must have exactly one author: either a teacher or an admin");
    }
  }
}
