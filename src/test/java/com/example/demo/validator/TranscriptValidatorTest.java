package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.exception.TranscriptValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TranscriptValidatorTest {

  private final TranscriptValidator validator = new TranscriptValidator();

  @Test
  void student_can_access_own_transcript() {
    var studentId = UUID.randomUUID();
    assertThatCode(() -> validator.validateRequesterCanAccess(studentId, false, studentId))
        .doesNotThrowAnyException();
  }

  @Test
  void admin_can_access_any_transcript() {
    assertThatCode(
            () -> validator.validateRequesterCanAccess(UUID.randomUUID(), true, UUID.randomUUID()))
        .doesNotThrowAnyException();
  }

  @Test
  void student_cannot_access_another_students_transcript() {
    assertThatThrownBy(
            () -> validator.validateRequesterCanAccess(UUID.randomUUID(), false, UUID.randomUUID()))
        .isInstanceOf(TranscriptValidationException.class);
  }
}
