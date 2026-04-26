package pt.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI (Swagger) documentation.
 */
@Configuration
public class OpenAPIConfig {

  @Value("${swagger.url}")
  private String baseUrl;
  @Value("${swagger.title}")
  private String title;
  @Value("${swagger.version}")
  private String version;
  @Value("${swagger.description}")
  private String description;

  /**
   * Configures the custom OpenAPI bean for documentation generation.
   *
   * @return a configured OpenAPI instance
   */
  @Bean
  public OpenAPI customOpenAPI() {
    final int currentYear = LocalDate.now().getYear();
    return new OpenAPI()
        .servers(List.of(new Server().url(baseUrl)))
        .info(new Info()
            .title(title)
            .version(version)
            .description(description)
            .license(new License().name(
                "Product Recommendation Service - All rights reserved " + currentYear)));
  }

}
