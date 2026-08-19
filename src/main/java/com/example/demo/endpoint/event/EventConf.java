package com.example.demo.endpoint.event;

import com.example.demo.PojaGenerated;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * NOTE: like the pre-fix {@code BucketConf}/{@code EmailConf}, this class never overrode the SQS
 * client's endpoint. Fixed to mirror {@code EventProducer.Conf}'s {@code AWS_ENDPOINT_URL} pattern
 * -- otherwise the worker would try to poll real AWS SQS instead of LocalStack.
 */
@PojaGenerated
@Configuration
@Slf4j
public class EventConf {
  private final Region region;

  public EventConf(@Value("eu-west-3") Region region) {
    this.region = region;
  }

  @Bean
  public SqsClient getSqsClient() {
    var builder = SqsClient.builder().region(region);
    String endpointOverride = System.getenv("AWS_ENDPOINT_URL");
    if (endpointOverride != null && !endpointOverride.isBlank()) {
      log.info("Overriding SQS endpoint with: {}", endpointOverride);
      builder.endpointOverride(URI.create(endpointOverride));
    }
    return builder.build();
  }
}
