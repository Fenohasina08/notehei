package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Transcript(
    UUID id,
    UUID studentId,
    UUID semesterId,
    String s3Url,
    String status,
    LocalDateTime generatedAt) {}
