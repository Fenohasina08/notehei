package com.example.demo.conf;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.demo.PojaGenerated;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@PojaGenerated
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Slf4j
public abstract class FacadeIT {

  protected static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("notehei_test")
          .withUsername("test")
          .withPassword("test");

  static {
    POSTGRES.start();
  }

  @Autowired protected DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  protected JdbcTemplate jdbcTemplate() {
    if (jdbcTemplate == null) {
      jdbcTemplate = new JdbcTemplate(dataSource);
    }
    return jdbcTemplate;
  }

  @SneakyThrows
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);

    new EventConf().configureProperties(registry);
    new BucketConf().configureProperties(registry);
    new EmailConf().configureProperties(registry);

    try {
      var envConfClazz = Class.forName("com.example.demo.conf.EnvConf");
      var envConfConfigureProperties =
          envConfClazz.getDeclaredMethod("configureProperties", DynamicPropertyRegistry.class);
      var envConf = envConfClazz.getConstructor().newInstance();
      envConfConfigureProperties.invoke(envConf, registry);
    } catch (ClassNotFoundException e) {
      log.warn("EnvConf missing: no project-specific test env vars will be set");
    }
  }

  @BeforeEach
  void cleanDatabaseBeforeEach() {
    cleanDatabase();
  }

  protected void cleanDatabase() {
    var jdbc = jdbcTemplate();

    jdbc.update("DELETE FROM grade_history");
    jdbc.update("DELETE FROM grade");
    jdbc.update("DELETE FROM teaching_assignment");
    jdbc.update("DELETE FROM exam");

    jdbc.update("DELETE FROM course_unit_program");
    jdbc.update("DELETE FROM course_unit_course");

    jdbc.update("DELETE FROM course_unit");
    jdbc.update("DELETE FROM course");

    jdbc.update("DELETE FROM transcript");

    jdbc.update("DELETE FROM group_program_history");
    jdbc.update("DELETE FROM group_membership");

    jdbc.update("DELETE FROM teacher");
    jdbc.update("DELETE FROM student");
    jdbc.update("DELETE FROM admin");

    jdbc.update("DELETE FROM semester");
    jdbc.update("DELETE FROM academic_year");
    jdbc.update("DELETE FROM program");

    jdbc.update("DELETE FROM \"group\"");
    jdbc.update("DELETE FROM cohort");
  }
}
