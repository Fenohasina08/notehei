package com.example.demo.file.pdf;

import java.math.BigDecimal;
import java.util.List;

public record CourseUnitSection(
    String code, String name, Integer credits, BigDecimal average, List<CourseLine> courses) {}
