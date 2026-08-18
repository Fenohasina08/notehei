package com.example.demo.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CourseGrade(UUID courseId, Integer credits, BigDecimal grade) {}
