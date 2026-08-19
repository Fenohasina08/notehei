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

  @Autowired private DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  protected JdbcTemplate jdbcTemplate() {
    if (jdbcTemplate == null) {
      jdbcTemplate = new JdbcTemplate(dataSource);
    }
    return jdbcTemplate;
  }

  /**
   * Nettoyage complet et centralisé de la base, dans l'ordre FK correct (tables enfants avant
   * tables parentes). Utilisé par toutes les classes IT pour éviter les conflits FK entre classes
   * tournant contre la même base.
   */
  protected void cleanDatabase() {
    var jdbc = jdbcTemplate();
    jdbc.update("DELETE FROM grade_history");
    jdbc.update("DELETE FROM grade");
    jdbc.update("DELETE FROM teaching_assignment");
    jdbc.update("DELETE FROM exam");
    jdbc.update("DELETE FROM course_unit_course");
    jdbc.update("DELETE FROM course_unit");
    jdbc.update("DELETE FROM course");
    jdbc.update("DELETE FROM transcript");
    jdbc.update("DELETE FROM teacher");
    jdbc.update("DELETE FROM student");
    jdbc.update("DELETE FROM admin");
    jdbc.update("DELETE FROM semester");
    jdbc.update("DELETE FROM academic_year");
    jdbc.update("DELETE FROM \"group\"");
    jdbc.update("DELETE FROM cohort");
  }

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
}
