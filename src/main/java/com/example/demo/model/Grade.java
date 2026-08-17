package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Grade(
    UUID id,
    String studentMatricule,
    UUID examId,
    BigDecimal value,
    String status,
    LocalDateTime enteredAt,
    String teacherMatricule,
    UUID adminId) {}
