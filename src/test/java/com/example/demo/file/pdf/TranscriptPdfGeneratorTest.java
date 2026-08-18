package com.example.demo.file.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

@Slf4j
class TranscriptPdfGeneratorTest {

  private final TranscriptPdfGenerator generator = new TranscriptPdfGenerator();

  @Test
  void generate_shouldProduceANonEmptyPdfFile() throws IOException {
    var student = sampleStudent();
    var semester = sampleSemester();
    var academicYear = sampleAcademicYear();
    var sections = sampleSections();

    var file =
        generator.generate(student, semester, academicYear, sections, new BigDecimal("14.10"));

    assertThat(file).exists();
    assertThat(file.length()).isGreaterThan(0);
  }

  @Test
  void generate_shouldContainStudentAndGradeDetailsInTheText() throws IOException {
    var student = sampleStudent();
    var semester = sampleSemester();
    var academicYear = sampleAcademicYear();
    var sections = sampleSections();

    var file =
        generator.generate(student, semester, academicYear, sections, new BigDecimal("14.10"));

    var text = extractText(file);

    assertThat(text).contains("Herimamy");
    assertThat(text).contains("STD26123456");
    assertThat(text).contains("PROG101");
    assertThat(text).contains("Java Fundamentals");
    assertThat(text).contains("UE-PROG");
    assertThat(text).contains("Semester average");
    assertThat(text).contains("14.10");
  }

  @Test
  void generate_savesACopyForManualVisualInspection() throws IOException {
    var student = sampleStudent();
    var semester = sampleSemester();
    var academicYear = sampleAcademicYear();
    var sections = sampleSections();

    var file =
        generator.generate(student, semester, academicYear, sections, new BigDecimal("14.10"));

    var target = Path.of("build/test-output-transcript.pdf");
    Files.createDirectories(target.getParent());
    Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

    log.info("Generated PDF copied to: {}", target.toAbsolutePath());

    assertThat(target).exists();
  }

  private String extractText(java.io.File file) throws IOException {
    try (var document = Loader.loadPDF(file)) {
      return new PDFTextStripper().getText(document);
    }
  }

  private JStudent sampleStudent() {
    return JStudent.builder()
        .id(UUID.randomUUID())
        .firstName("Herimamy")
        .lastName("Fenohasina")
        .matricule("STD26123456")
        .email("herimamy@test.com")
        .build();
  }

  private JSemester sampleSemester() {
    return JSemester.builder()
        .id(UUID.randomUUID())
        .number(1)
        .cohortId(UUID.randomUUID())
        .academicYearId(UUID.randomUUID())
        .build();
  }

  private JAcademicYear sampleAcademicYear() {
    return JAcademicYear.builder()
        .id(UUID.randomUUID())
        .name("2026-2027")
        .startYear(2026)
        .endYear(2027)
        .build();
  }

  private List<CourseUnitSection> sampleSections() {
    return List.of(
        new CourseUnitSection(
            "UE-PROG",
            "Programming",
            10,
            new BigDecimal("14.10"),
            List.of(
                new CourseLine("PROG101", "Java Fundamentals", 6, new BigDecimal("15.50")),
                new CourseLine("PROG102", "Web Development", 4, new BigDecimal("12.00")))));
  }
}
