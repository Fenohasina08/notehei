package com.example.demo.model;

import java.time.LocalDate;
import java.util.UUID;

public record GroupProgramHistory(
    UUID id, UUID groupId, UUID programId, LocalDate startDate, LocalDate endDate) {}
