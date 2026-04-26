package pt.challenge.dto;

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
