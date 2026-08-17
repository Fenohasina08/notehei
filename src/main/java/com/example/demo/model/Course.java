package com.example.demo.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Course(UUID id, String reference, String title, BigDecimal coefficient) {}
