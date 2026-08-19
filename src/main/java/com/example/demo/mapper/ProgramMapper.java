package com.example.demo.mapper;

import com.example.demo.entity.JProgram;
import com.example.demo.model.Program;
import org.springframework.stereotype.Component;

@Component
public class ProgramMapper {

  public JProgram toEntity(Program dto) {
    return JProgram.builder().id(dto.id()).code(dto.code()).name(dto.name()).build();
  }

  public Program toDto(JProgram entity) {
    return new Program(entity.getId(), entity.getCode(), entity.getName());
  }
}
