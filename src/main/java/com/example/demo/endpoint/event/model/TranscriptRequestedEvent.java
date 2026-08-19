package com.example.demo.endpoint.event.model;

import java.time.Duration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Requests the asynchronous generation of a student's PDF transcript for a given semester. Follows
 * the Poja convention: event classes live in {@code com.example.demo.endpoint.event.model}, and are
 * produced via {@code EventProducer} (cf. "Hello world, but with asynchronous reply by email").
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class TranscriptRequestedEvent extends PojaEvent {
  private UUID transcriptId;
  private UUID studentId;
  private UUID semesterId;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(45);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
