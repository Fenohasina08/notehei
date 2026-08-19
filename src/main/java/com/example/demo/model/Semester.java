package com.example.demo.model;

import java.util.UUID;

public record Semester(UUID id, Integer number, UUID cohortId, UUID academicYearId) {}
