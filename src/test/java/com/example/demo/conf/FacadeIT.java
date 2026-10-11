package com.example.demo.conf;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.demo.PojaGenerated;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@PojaGenerated
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
public class FacadeIT {

  @Autowired(required = false)
  private DataSource dataSource;

  private JdbcTemplate jdbcTemplateInstance;

  @SneakyThrows
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {

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

  /** Returns a JdbcTemplate instance for database operations */
  protected JdbcTemplate jdbcTemplate() {
    if (jdbcTemplateInstance == null && dataSource != null) {
      jdbcTemplateInstance = new JdbcTemplate(dataSource);
    }
    return jdbcTemplateInstance;
  }

  /** Cleans the database by clearing all relevant tables */
  protected void cleanDatabase() {
    if (jdbcTemplate() != null) {
      jdbcTemplate().update("DELETE FROM grade");
      jdbcTemplate().update("DELETE FROM exam");
      jdbcTemplate().update("DELETE FROM course_unit_course");
      jdbcTemplate().update("DELETE FROM course_unit");
      jdbcTemplate().update("DELETE FROM course");
      jdbcTemplate().update("DELETE FROM semester");
      jdbcTemplate().update("DELETE FROM academic_year");
      jdbcTemplate().update("DELETE FROM cohort");
      jdbcTemplate().update("DELETE FROM teacher");
      jdbcTemplate().update("DELETE FROM student");
    }
  }
}
