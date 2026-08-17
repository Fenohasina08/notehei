package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.exception.GradeValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GradeHistoryValidatorTest {

  private final GradeHistoryValidator validator = new GradeHistoryValidator();

  @Test
  void accepts_teacher_only() {
    assertThatCode(() -> validator.validateExactlyOneAuthor("TCH26183", null))
        .doesNotThrowAnyException();
  }

  @Test
  void accepts_admin_only() {
    assertThatCode(() -> validator.validateExactlyOneAuthor(null, UUID.randomUUID()))
        .doesNotThrowAnyException();
  }

  @Test
  void rejects_both_set() {
    assertThatThrownBy(() -> validator.validateExactlyOneAuthor("TCH26183", UUID.randomUUID()))
        .isInstanceOf(GradeValidationException.class);
  }

  @Test
  void rejects_neither_set() {
    assertThatThrownBy(() -> validator.validateExactlyOneAuthor(null, null))
        .isInstanceOf(GradeValidationException.class);
  }
}
