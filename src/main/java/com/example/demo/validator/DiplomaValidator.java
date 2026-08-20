package com.example.demo.validator;

import com.example.demo.exception.DiplomaValidationException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class DiplomaValidator {

  public void validateSemesters(Set<Integer> validatedSemesterNumbers) {
    if (!validatedSemesterNumbers.containsAll(Set.of(1, 2, 3, 4, 5, 6))) {
      throw new DiplomaValidationException(
          "L'étudiant n'a pas validé tous les semestres de S1 à S6.");
    }
  }
}
