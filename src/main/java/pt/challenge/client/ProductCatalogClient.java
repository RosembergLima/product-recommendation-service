package pt.challenge.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pt.challenge.dto.external.product.ProductCategoryResponse;
import pt.challenge.dto.external.user.UserProfileResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCatalogClient {

  private final RestClient productCatalogRestClient;

  /**
   * Fetches products for a specific category from the external catalog.
   *
   * @param category the product category
   * @return the product category response
   */
  @Cacheable(value = "products", key = "#category")
  @CircuitBreaker(name = "productCatalog", fallbackMethod = "getProductsByCategoryFallback")
  public ProductCategoryResponse getProductsByCategory(String category) {
    log.info("Requesting products for category: {}", category);
    return productCatalogRestClient.get()
        .uri("/api/products/category/{category}", category)
        .retrieve()
        .body(ProductCategoryResponse.class);
  }

  /**
   * Fallback method for getProductsByCategory when the circuit breaker is open or the service fails.
   *
   * @param category the product category
   * @param t the throwable cause
   * @return an empty product category response
   */
  public ProductCategoryResponse getProductsByCategoryFallback(String category, Throwable t) {
    log.error("Failed to fetch products for category {}. Reason: {}. Using empty fallback.", category, t.getMessage(), t);
    return new ProductCategoryResponse(category, 0, Set.of(), null);
  }
}
