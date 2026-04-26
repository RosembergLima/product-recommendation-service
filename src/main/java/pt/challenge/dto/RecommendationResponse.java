package pt.challenge.dto;

import java.util.List;

public record RecommendationResponse(
    List<RecommendationDto> recommendations, short totalRecommendations) {

}
