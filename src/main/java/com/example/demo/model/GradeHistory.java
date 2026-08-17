package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GradeHistory(
    UUID id,
    UUID gradeId,
    BigDecimal oldValue,
    BigDecimal newValue,
    String reason,
    LocalDateTime modifiedAt,
    String teacherMatricule,
    UUID adminId) {}
