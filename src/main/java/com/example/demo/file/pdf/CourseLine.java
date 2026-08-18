package com.example.demo.file.pdf;

import java.math.BigDecimal;

public record CourseLine(
    String courseReference, String courseTitle, Integer credits, BigDecimal grade) {}
