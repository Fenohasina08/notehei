package com.example.demo.model;

import java.util.UUID;

public record TeachingAssignment(UUID id, UUID teacherId, UUID courseId, UUID groupId) {}
