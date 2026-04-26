package pt.challenge.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import pt.challenge.dto.RecommendationDto;
import pt.challenge.dto.external.product.ProductCategoryResponse;

@Component
public class RecommendationMapper {

  /**
   * Maps an external product item to an internal recommendation DTO.
   *
   * @param userId the user ID
   * @param category the product category
   * @param product the product item from external service
   * @param reason the reason for the recommendation
   * @return a mapped RecommendationDto
   */
  public RecommendationDto toDto(String userId, String category,
      ProductCategoryResponse.ProductItem product, String reason) {
    double discount = product.originalPrice() - product.currentPrice();
    String discountStr = String.format("%.2f", discount);

    return new RecommendationDto(
        userId,
        product.productId(),
        product.name(),
        category,
        String.valueOf(product.currentPrice()),
        String.valueOf(product.originalPrice()),
        discountStr,
        String.valueOf(product.averageRating()),
        String.valueOf(product.totalReviews()),
        product.availability(),
        reason,
        LocalDateTime.now().toString()
    );
  }
}
