package pt.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for external service communication and resilience.
 * <p>
 * This class defines the RestClient beans for each external service, applying
 * specific timeout configurations to ensure the system remains responsive.
 * </p>
 */
@Configuration
public class ResilienceConfig {

  @Value("${external.services.user-profile.url:http://localhost:8081}")
  private String userProfileBaseUrl;

  @Value("${external.services.product-catalog.url:http://localhost:8081}")
  private String productCatalogBaseUrl;

  /**
   * Configures a RestClient for the User Profile service with specific timeouts.
   *
   * @return a configured RestClient
   */
  @Bean
  public RestClient userProfileRestClient() {
    return RestClient.builder()
        .baseUrl(userProfileBaseUrl)
        .requestFactory(getClientRequestFactory(2500)) // 2.5s for slow service
        .build();
  }

  /**
   * Configures a RestClient for the Product Catalog service with specific timeouts.
   *
   * @return a configured RestClient
   */
  @Bean
  public RestClient productCatalogRestClient() {
    return RestClient.builder()
        .baseUrl(productCatalogBaseUrl)
        .requestFactory(getClientRequestFactory(1000)) // 1s for faster service
        .build();
  }

  /**
   * Creates a request factory with specified connect and read timeouts.
   *
   * @param timeout timeout in milliseconds
   * @return the configured request factory
   */
  private org.springframework.http.client.ClientHttpRequestFactory getClientRequestFactory(int timeout) {
    org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(timeout);
    factory.setReadTimeout(timeout);
    return factory;
  }
}
