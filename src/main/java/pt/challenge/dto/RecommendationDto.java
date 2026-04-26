package pt.challenge.dto;

import lombok.Builder;

/**
 * Data Transfer Object representing a product recommendation.
 *
 * @param userId the ID of the user
 * @param productId the ID of the product
 * @param productName the name of the product
 * @param category the category of the product
 * @param currentPrice the current price as a string
 * @param originalPrice the original price as a string
 * @param discount the discount amount as a string
 * @param averageRating the average rating as a string
 * @param totalReviews the total number of reviews as a string
 * @param availability the current availability status
 * @param recommendationReason the reason why this product was recommended
 * @param generatedAt the timestamp when the recommendation was generated
 */
@Builder
public record RecommendationDto(
    String userId,
    String productId,
    String productName,
    String category,
    String currentPrice,
    String originalPrice,
    String discount,
    String averageRating,
    String totalReviews,
    String availability,
    String recommendationReason,
    String generatedAt
) {

}
