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
import pt.challenge.dto.external.product.ProductCatalogDto;
import pt.challenge.dto.external.product.ProductCategoryResponse;
import pt.challenge.dto.external.user.UserProfileResponse;
import pt.challenge.mapper.ProductCatalogMapper;

/**
 * Client implementation for interacting with the External Product Catalog Service.
 * <p>
 * This client manages product retrieval by category, implementing a Circuit Breaker
 * to handle service instability and a short-lived cache for pricing accuracy.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCatalogClient implements ExternalCatalogClient {

  private final RestClient productCatalogRestClient;
  private final ProductCatalogMapper mapper;

  /**
   * Fetches products for a specific category from the external catalog.
   *
   * @param category the product category
   * @return the product category response
   */

  @Override
  @Cacheable(value = "products", key = "#category")
  @CircuitBreaker(name = "productCatalog", fallbackMethod = "getProductsByCategoryFallback")
  public ProductCatalogDto getProductsByCategory(String category) {
    log.info("Requesting products for category: {}", category);
    ProductCategoryResponse productCategoryResponse = productCatalogRestClient.get()
        .uri("/api/products/category/{category}", category)
        .retrieve()
        .body(ProductCategoryResponse.class);
    return mapper.toDto(productCategoryResponse);
  }

  /**
   * Fallback method for getProductsByCategory when the circuit breaker is open or the service fails.
   *
   * @param category the product category
   * @param t the throwable cause
   * @return an empty product category response
   */
  @Override
  public ProductCatalogDto getProductsByCategoryFallback(String category, Throwable t) {
    log.warn("Failed to fetch products for category {}. Reason: {}. Using empty fallback.", category, t.getMessage());
    ProductCategoryResponse fallbackResponse = new ProductCategoryResponse(
        category,
        0,
        Set.of(),
        null
    );
    return mapper.toDto(fallbackResponse);
  }
}
