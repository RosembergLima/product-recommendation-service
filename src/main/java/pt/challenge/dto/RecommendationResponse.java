package pt.challenge.dto;

import java.util.List;

/**
 * Response object containing a list of recommendations and the total count.
 *
 * @param recommendations the list of product recommendations
 * @param totalRecommendations the total number of recommendations in the list
 */
public record RecommendationResponse(
    List<RecommendationDto> recommendations, int totalRecommendations) {

}
