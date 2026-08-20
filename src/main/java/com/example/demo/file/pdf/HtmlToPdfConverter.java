package com.example.demo.file.pdf;

import static java.io.File.createTempFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class HtmlToPdfConverter implements Function<String, File> {

  @SneakyThrows
  @Override
  public File apply(String html) {
    var pdfFile = createTempFile("transcript-", ".pdf");
    try (var outputStream = new FileOutputStream(pdfFile)) {
      var builder = new PdfRendererBuilder();
      builder.useFastMode();
      builder.withHtmlContent(html, null);
      builder.toStream(outputStream);
      builder.run();
    }
    return pdfFile;
  }
}
