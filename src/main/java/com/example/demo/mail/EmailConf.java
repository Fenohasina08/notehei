package com.example.demo.mail;

import com.example.demo.PojaGenerated;
import java.net.URI;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@PojaGenerated
@Configuration
public class EmailConf {

  @Getter private final String sesSource;
  private final Region region;
  private final URI endpoint;

  public EmailConf(
      @Value("noreply@poja.io") String sesSource,
      @Value("eu-west-3") Region region,
      @Value("${aws.endpoint-url:}") String endpointUrl) {
    this.sesSource = sesSource;
    this.region = region;
    this.endpoint = endpointUrl.isBlank() ? null : URI.create(endpointUrl);
  }

  @Bean
  public SesClient getSesClient() {
    var builder = SesClient.builder().region(region);
    return endpoint == null ? builder.build() : builder.endpointOverride(endpoint).build();
  }
}
