package com.example.demo.model;

import java.time.LocalDate;
import java.util.UUID;

public record GroupMembership(
    UUID id, UUID studentId, UUID groupId, LocalDate startDate, LocalDate endDate) {}
