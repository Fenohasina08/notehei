package com.example.demo.service;

import com.example.demo.exception.ProgramNotFoundException;
import com.example.demo.exception.ProgramValidationException;
import com.example.demo.mapper.ProgramMapper;
import com.example.demo.model.Program;
import com.example.demo.repository.ProgramRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProgramService {

  private final ProgramRepository programRepository;
  private final ProgramMapper programMapper;

  public Program create(Program program) {
    if (programRepository.existsByCode(program.code())) {
      throw new ProgramValidationException("A program already exists with code " + program.code());
    }
    var entity = programMapper.toEntity(program);
    return programMapper.toDto(programRepository.save(entity));
  }

  public List<Program> getAll() {
    return programRepository.findAll().stream().map(programMapper::toDto).toList();
  }

  public Program getById(UUID id) {
    return programRepository
        .findById(id)
        .map(programMapper::toDto)
        .orElseThrow(() -> new ProgramNotFoundException(id));
  }

  public void delete(UUID id) {
    programRepository.deleteById(id);
  }
}
