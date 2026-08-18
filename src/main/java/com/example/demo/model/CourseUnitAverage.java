package com.example.demo.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CourseUnitAverage(UUID courseUnitId, Integer credits, BigDecimal average) {}
