package pt.challenge.dto.external.product;

import java.util.Set;
import lombok.Builder;

/**
 * Data Transfer Object for a category of products.
 *
 * @param category the name of the category
 * @param products the set of products within this category
 */
@Builder
public record ProductCatalogDto(String category, Set<ProductDto> products) {

  /**
   * Data Transfer Object for an individual product.
   *
   * @param productId the ID of the product
   * @param name the name of the product
   * @param currentPrice current selling price
   * @param originalPrice original price before discounts
   * @param averageRating average customer rating
   * @param availability current stock status
   * @param totalReviews total number of reviews
   */
  @Builder
  public record ProductDto(
      String productId,
      String name,
      Double currentPrice,
      Double originalPrice,
      Double averageRating,
      String availability,
      String totalReviews
  ){}

}
