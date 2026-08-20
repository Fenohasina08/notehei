package com.example.demo.service;

import com.example.demo.entity.JSemester;
import com.example.demo.entity.JStudent;
import com.example.demo.file.pdf.CourseLine;
import com.example.demo.file.pdf.CourseUnitSection;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.CourseUnitRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.repository.StudentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Assembles the data computed by {@link GradeService} (retained grades, course unit averages,
 * semester average) into an XHTML document ready to be rendered as a PDF transcript.
 */
@Service
@AllArgsConstructor
public class TranscriptPdfService {

  private final GradeService gradeService;
  private final StudentRepository studentRepository;
  private final SemesterRepository semesterRepository;
  private final CourseUnitRepository courseUnitRepository;
  private final CourseRepository courseRepository;

  public String buildTranscriptHtml(UUID studentId, UUID semesterId) {
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
    var semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semesterId));

    var sections = buildCourseUnitSections(student.getMatricule(), semesterId);
    var semesterAverage = gradeService.computeSemesterAverage(student.getMatricule(), semesterId);

    return renderHtml(student, semester, sections, semesterAverage);
  }

  private List<CourseUnitSection> buildCourseUnitSections(
      String studentMatricule, UUID semesterId) {
    return gradeService.getCourseUnitAveragesForSemester(studentMatricule, semesterId).stream()
        .map(
            courseUnitAverage -> {
              var courseUnit =
                  courseUnitRepository
                      .findById(courseUnitAverage.courseUnitId())
                      .orElseThrow(
                          () ->
                              new IllegalArgumentException(
                                  "Course unit not found: " + courseUnitAverage.courseUnitId()));
              var courseLines =
                  gradeService
                      .getCourseGradesForCourseUnit(studentMatricule, courseUnit.getId())
                      .stream()
                      .map(
                          courseGrade ->
                              toCourseLine(
                                  courseGrade.courseId(),
                                  courseGrade.credits(),
                                  courseGrade.grade()))
                      .toList();
              return new CourseUnitSection(
                  courseUnit.getCode(),
                  courseUnit.getName(),
                  courseUnit.getCredits(),
                  courseUnitAverage.average(),
                  courseLines);
            })
        .toList();
  }

  private CourseLine toCourseLine(UUID courseId, Integer credits, BigDecimal grade) {
    var course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
    return new CourseLine(course.getReference(), course.getTitle(), credits, grade);
  }

  private String renderHtml(
      JStudent student,
      JSemester semester,
      List<CourseUnitSection> sections,
      BigDecimal semesterAverage) {
    var sectionsHtml = new StringBuilder();
    for (var section : sections) {
      sectionsHtml.append(renderSection(section));
    }

    return """
           <html>
             <body style="font-family: Helvetica, Arial, sans-serif; font-size: 12px;">
               <h1>Relevé de notes</h1>
               <p><strong>Étudiant :</strong> %s %s (%s)</p>
               <p><strong>Semestre :</strong> %d</p>
               %s
               <h2>Moyenne générale du semestre : %s / 20</h2>
             </body>
           </html>
           """
        .formatted(
            student.getFirstName(),
            student.getLastName(),
            student.getMatricule(),
            semester.getNumber(),
            sectionsHtml,
            semesterAverage.toPlainString());
  }

  private String renderSection(CourseUnitSection section) {
    var rows = new StringBuilder();
    for (var line : section.courses()) {
      rows.append(
          """
          <tr>
            <td>%s</td><td>%s</td><td>%d</td><td>%s</td>
          </tr>
          """
              .formatted(
                  line.courseReference(),
                  line.courseTitle(),
                  line.credits(),
                  line.grade().toPlainString()));
    }

    return """
<h3>%s — %s (%d crédits, moyenne %s / 20)</h3>
<table border="1" cellspacing="0" cellpadding="4" style="width:100%%; border-collapse: collapse;">
  <thead>
    <tr><th>Réf.</th><th>Cours</th><th>Crédits</th><th>Note</th></tr>
  </thead>
  <tbody>
    %s
  </tbody>
</table>
"""
        .formatted(
            section.code(),
            section.name(),
            section.credits(),
            section.average().toPlainString(),
            rows);
  }
}
