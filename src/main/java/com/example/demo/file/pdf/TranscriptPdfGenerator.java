package com.example.demo.file.pdf;

import com.example.demo.entity.JAcademicYear;
import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

@Component
public class TranscriptPdfGenerator {

  public File generate(
      JStudent student,
      JSemester semester,
      JAcademicYear academicYear,
      List<CourseUnitSection> sections,
      BigDecimal semesterAverage)
      throws IOException {

    File tempFile = File.createTempFile("transcript_", ".pdf");

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage();
      document.addPage(page);

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.setLeading(16.0f);
        contentStream.newLineAtOffset(50, 750);

        if (student != null) {
          contentStream.showText(
              "Student: " + student.getFirstName() + " " + student.getLastName());
          contentStream.newLine();

          contentStream.showText("Matricule: " + student.getMatricule());
          contentStream.newLine();
        }

        if (academicYear != null) {
          contentStream.showText("Academic Year: " + academicYear.getName());
          contentStream.newLine();
        }

        if (semester != null) {
          contentStream.showText("Semester: " + semester.getNumber());
          contentStream.newLine();
        }

        contentStream.newLine();

        if (sections != null) {
          for (CourseUnitSection section : sections) {

            contentStream.showText("UE Code: " + section.code() + " - " + section.name());
            contentStream.newLine();

            if (section.courses() != null) {
              for (CourseLine course : section.courses()) {

                contentStream.showText(
                    "  - "
                        + course.courseReference()
                        + ": "
                        + course.courseTitle()
                        + " ("
                        + course.grade()
                        + ")");
                contentStream.newLine();
              }
            }
          }
        }

        contentStream.newLine();
        contentStream.showText("Semester average: " + semesterAverage);

        contentStream.endText();
      }

      document.save(tempFile);
    }

    return tempFile;
  }
}
