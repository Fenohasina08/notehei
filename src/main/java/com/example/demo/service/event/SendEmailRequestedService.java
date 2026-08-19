package com.example.demo.endpoint.event;

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
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  private final Mailer mailer;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {
    var recipientAddress = new InternetAddress(event.getTo());

    List<File> attachments = List.of();
    if (event.getAttachmentBucketKey() != null && !event.getAttachmentBucketKey().isBlank()) {
      File downloadedPdf = bucketComponent.download(event.getAttachmentBucketKey());
      attachments = List.of(downloadedPdf);
    }

    var email =
        new Email(
            recipientAddress,
            List.of(),
            List.of(),
            event.getSubject(),
            event.getHtmlBody(),
            attachments);

    mailer.accept(email);
  }
}
