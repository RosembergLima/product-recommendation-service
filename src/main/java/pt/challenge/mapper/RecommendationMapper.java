package pt.challenge.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import pt.challenge.dto.RecommendationDto;
import pt.challenge.dto.external.product.ProductCatalogDto.ProductDto;

/**
 * Mapper for creating Recommendation DTOs from product and user information.
 * <p>
 * This class includes the business logic for calculating discounts and
 * formatting recommendation details.
 * </p>
 */
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
      ProductDto product, String reason) {
    double discount = product.originalPrice() - product.currentPrice();
    String discountStr = String.format("%.2f", discount);

    return RecommendationDto.builder()
        .userId(userId)
        .productId(product.productId())
        .productName(product.name())
        .category(category)
        .currentPrice(String.valueOf(product.currentPrice()))
        .originalPrice(String.valueOf(product.originalPrice()))
        .discount(discountStr)
        .averageRating(String.valueOf(product.averageRating()))
        .totalReviews(product.totalReviews())
        .availability(product.availability())
        .recommendationReason(reason)
        .generatedAt(LocalDateTime.now().toString())
        .build();
  }
}
