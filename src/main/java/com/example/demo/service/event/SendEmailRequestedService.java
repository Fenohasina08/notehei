package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {

  private final Mailer mailer;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {

    log.info("Sending email to {} with subject {}", event.getTo(), event.getSubject());

    List<File> attachments = List.of();

    if (event.getAttachmentBucketKey() != null && !event.getAttachmentBucketKey().isBlank()) {

      File downloadedPdf = bucketComponent.download(event.getAttachmentBucketKey());

      attachments = List.of(downloadedPdf);
    }

    var email =
        new Email(
            new InternetAddress(event.getTo()),
            List.of(),
            List.of(),
            event.getSubject(),
            event.getHtmlBody(),
            attachments);

    mailer.accept(email);

    log.info("Email successfully sent to {}", event.getTo());
  }
}
