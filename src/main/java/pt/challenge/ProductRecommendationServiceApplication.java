package pt.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The entry point for the Product Recommendation Service application.
 * <p>
 * This class initializes the Spring Boot application, enabling caching and 
 * scanning for all components in the base package.
 * </p>
 */
@EnableCaching
@SpringBootApplication
public class ProductRecommendationServiceApplication {

  /**
   * Main method to start the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ProductRecommendationServiceApplication.class, args);
  }

}
