package pt.challenge.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.challenge.client.ProductCatalogClient;
import pt.challenge.client.UserProfileClient;
import pt.challenge.dto.FeedbackRequest;
import pt.challenge.dto.FeedbackResponse;
import pt.challenge.dto.RecommendationDto;
import pt.challenge.dto.RecommendationResponse;
import pt.challenge.dto.external.product.ProductCategoryResponse;
import pt.challenge.dto.external.user.UserProfileResponse;
import pt.challenge.entity.Feedback;
import pt.challenge.mapper.RecommendationMapper;
import pt.challenge.repository.FeedbackRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

  private final FeedbackRepository feedbackRepository;
  private final UserProfileClient userProfileClient;
  private final ProductCatalogClient productCatalogClient;
  private final RecommendationMapper recommendationMapper;

  /**
   * Records user feedback for a product recommendation.
   *
   * @param feedbackRequest the feedback data containing user, product, and feedback type
   * @return a response indicating success or failure
   */
  public FeedbackResponse save(FeedbackRequest feedbackRequest) {
    log.info("Recording feedback for user: {} and product: {}", feedbackRequest.userId(), feedbackRequest.productId());
    Feedback feedback = Feedback.builder()
        .userId(feedbackRequest.userId())
        .productId(feedbackRequest.productId())
        .feedback(feedbackRequest.feedback().toString())
        .comment(feedbackRequest.comment())
        .build();
    feedbackRepository.save(feedback);
    log.debug("Feedback successfully saved to database for product: {}", feedbackRequest.productId());
    return new FeedbackResponse("success", "Feedback recorded successfully");
  }

  /**
   * Generates personalized product recommendations for a specific user.
   *
   * @param userId the unique identifier of the user
   * @return a list of recommendation responses containing suggested products
   */
  public List<RecommendationResponse> getRecommendations(String userId) {
    log.info("Generating recommendations for user: {}", userId);

    List<RecommendationDto> allRecommendations = Collections.synchronizedList(new java.util.ArrayList<>());

    // 1. Fetch User Profile
    log.debug("Fetching profile for user: {} to determine preferences", userId);
    UserProfileResponse userProfile = userProfileClient.getUserProfile(userId);

    // 2. Fetch Products for Preferred Categories in Parallel
    log.debug("User {} preferred categories: {}", userId, userProfile.preferences().categories());
    List<CompletableFuture<Void>> futures = userProfile.preferences().categories().stream()
        .map(category -> CompletableFuture.supplyAsync(() -> productCatalogClient.getProductsByCategory(category))
            .thenAccept(categoryResponse -> {
              log.debug("Processing {} products for category: {}", categoryResponse.products().size(), categoryResponse.category());
              categoryResponse.products().forEach(product -> {
                // Price range filter (inclusive)
                boolean withinPriceRange =
                    product.currentPrice() >= userProfile.preferences().priceRange().min() &&
                        product.currentPrice() <= userProfile.preferences().priceRange().max();

                // Availability filter
                boolean isAvailable = List.of("IN_STOCK", "LOW_STOCK")
                    .contains(product.availability());

                if (withinPriceRange && isAvailable) {
                  log.debug("Product {} selected as recommendation for user {}", product.productId(), userId);
                  String reason = "Matches your preferred category: " + categoryResponse.category();

                  if (product.currentPrice() < product.originalPrice()) {
                    double discountPercent = Math.round(
                        (1 - product.currentPrice() / product.originalPrice()) * 100.0
                    );
                    reason = "Great deal with " + (int) discountPercent + "% discount";
                  } else if (product.averageRating() >= 4.0) {
                    reason = "Highly rated product in " + categoryResponse.category();
                  }

                  RecommendationDto recommendationDto = recommendationMapper.toDto(
                      userId, categoryResponse.category(), product, reason
                  );
                  allRecommendations.add(recommendationDto);
                }
              });
            }))
        .toList();

    // Wait for all requests and processing to complete
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    log.info("Successfully generated {} recommendations for user: {}", allRecommendations.size(), userId);
    // 5. Build Response
    return List.of(new RecommendationResponse(
        allRecommendations,
        (short) allRecommendations.size()
    ));
  }
}
