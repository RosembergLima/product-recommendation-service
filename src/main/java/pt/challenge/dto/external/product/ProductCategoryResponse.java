package pt.challenge.dto.external.product;

import java.util.Set;

public record ProductCategoryResponse(
    String category,
    Integer totalProducts,
    Set<ProductItem> products,
    Filters filters
) {

  public record ProductItem(
      String productId,
      String name,
      Double currentPrice,
      Double originalPrice,
      Double averageRating,
      Integer totalReviews,
      String availability
  ) {

  }

  public record Filters(
      PriceRange priceRange,
      Set<String> brands
  ) {

  }

  public record PriceRange(
      Double min,
      Double max
  ) {

  }
}
