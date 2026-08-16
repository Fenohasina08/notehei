package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Exam(
    UUID id, UUID courseId, ExamType type, LocalDateTime examDate, BigDecimal weighting) {}
