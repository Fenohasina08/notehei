package com.example.demo.model;

import java.util.UUID;

public record CourseUnit(UUID id, String code, String name, Integer credits, UUID semesterId) {}
