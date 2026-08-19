package com.example.demo.model;

import java.util.UUID;

public record StudentGroup(UUID id, String reference, UUID cohortId) {}
