package com.example.demo.endpoint.event;

import com.example.demo.PojaGenerated;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@PojaGenerated
@Configuration
public class EventConf {
  private final Region region;
  private final URI endpoint;

  @Autowired
  public EventConf(
      @Value("eu-west-3") Region region, @Value("${aws.endpoint-url:}") String endpointUrl) {
    this.region = region;
    this.endpoint = endpointUrl.isBlank() ? null : URI.create(endpointUrl);
  }

  public EventConf(Region region) {
    this(region, "");
  }

  @Bean
  public SqsClient getSqsClient() {
    var builder = SqsClient.builder().region(region);
    return endpoint == null ? builder.build() : builder.endpointOverride(endpoint).build();
  }
}
