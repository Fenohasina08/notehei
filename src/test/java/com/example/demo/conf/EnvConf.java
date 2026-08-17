package com.example.demo.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  public void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> System.getenv("DATABASE_URL"));
    registry.add("spring.datasource.username", () -> System.getenv("DATABASE_USERNAME"));
    registry.add("spring.datasource.password", () -> System.getenv("DATABASE_PASSWORD"));
  }
}
