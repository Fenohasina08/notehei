package com.example.demo.validator;

import com.example.demo.exception.TranscriptValidationException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TranscriptValidator {

  public void validateRequesterCanAccess(
      UUID requesterId, boolean requesterIsAdmin, UUID studentId) {
    if (!requesterIsAdmin && !requesterId.equals(studentId)) {
      throw new TranscriptValidationException(
          "Requester " + requesterId + " cannot access the transcript of " + studentId);
    }
  }
}
