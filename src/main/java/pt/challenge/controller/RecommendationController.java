package pt.challenge.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.challenge.dto.FeedbackRequest;
import pt.challenge.dto.FeedbackResponse;
import pt.challenge.dto.RecommendationResponse;
import pt.challenge.service.RecommendationService;

/**
 * REST Controller for managing product recommendations and user feedback.
 * <p>
 * Provides endpoints for retrieving personalized recommendations and
 * submitting user feedback on the recommended products.
 * </p>
 */
@RestController
@RequestMapping("api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RecommendationController.class);

  /**
   * Retrieves product recommendations for a specific user.
   *
   * @param userId the user ID
   * @return a list of recommendation responses
   */
  @GetMapping("/{userId}")
  public ResponseEntity<List<RecommendationResponse>> getRecommendations(
      @PathVariable String userId) {
    log.info("Received request for recommendations for user: {}", userId);
    List<RecommendationResponse> recommendations = recommendationService.getRecommendations(userId);
    log.debug("Returning {} recommendation groups for user: {}", recommendations.size(), userId);
    return ResponseEntity.ok(recommendations);
  }

  /**
   * Submits feedback for a product recommendation.
   *
   * @param feedbackRequest the feedback request details
   * @return the feedback response
   */
  @PostMapping("/feedback")
  public ResponseEntity<FeedbackResponse> postRecommendations(
      @Valid @RequestBody FeedbackRequest feedbackRequest) {
    log.info("Received feedback request for product: {} from user: {}", feedbackRequest.productId(), feedbackRequest.userId());
    FeedbackResponse response = recommendationService.save(feedbackRequest);
    log.debug("Feedback processed successfully with message: {}", response.message());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

}
