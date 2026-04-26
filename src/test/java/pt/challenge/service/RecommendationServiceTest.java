package pt.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import pt.challenge.util.FeedbackType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UserProfileClient userProfileClient;
    @Mock
    private ProductCatalogClient productCatalogClient;
    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void shouldSaveFeedback() {
        FeedbackRequest request = new FeedbackRequest("u1", "p1", FeedbackType.LIKED, "Cool");
        
        FeedbackResponse response = recommendationService.save(request);

        assertThat(response.status()).isEqualTo("success");
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void shouldGetRecommendationsWithVariousFilters() {
        String userId = "u1";
        UserProfileResponse profile = new UserProfileResponse(
                userId, null,
                new UserProfileResponse.Preferences(Set.of("cat1"), new UserProfileResponse.PriceRange(10, 100), Set.of(), "style"),
                null, null
        );

        ProductCategoryResponse.ProductItem p1 = new ProductCategoryResponse.ProductItem("p1", "Item 1", 50.0, 50.0, 3.5, 10, "IN_STOCK"); // Matches category (rating < 4.0)
        ProductCategoryResponse.ProductItem p2 = new ProductCategoryResponse.ProductItem("p2", "Item 2", 5.0, 5.0, 4.5, 10, "IN_STOCK");  // Price low
        ProductCategoryResponse.ProductItem p3 = new ProductCategoryResponse.ProductItem("p3", "Item 3", 150.0, 150.0, 4.5, 10, "IN_STOCK"); // Price high
        ProductCategoryResponse.ProductItem p4 = new ProductCategoryResponse.ProductItem("p4", "Item 4", 50.0, 50.0, 4.5, 10, "OUT_OF_STOCK"); // Out of stock
        ProductCategoryResponse.ProductItem p5 = new ProductCategoryResponse.ProductItem("p5", "Item 5", 40.0, 60.0, 3.0, 10, "IN_STOCK"); // Great deal (discount)
        ProductCategoryResponse.ProductItem p6 = new ProductCategoryResponse.ProductItem("p6", "Item 6", 40.0, 40.0, 4.2, 10, "LOW_STOCK"); // Highly rated (rating >= 4.0)

        ProductCategoryResponse cat1Response = new ProductCategoryResponse("cat1", 6, Set.of(p1, p2, p3, p4, p5, p6), null);

        when(userProfileClient.getUserProfile(userId)).thenReturn(profile);
        when(productCatalogClient.getProductsByCategory("cat1")).thenReturn(cat1Response);
        
        when(recommendationMapper.toDto(eq(userId), eq("cat1"), any(), anyString()))
                .thenAnswer(invocation -> {
                    ProductCategoryResponse.ProductItem item = invocation.getArgument(2);
                    String reason = invocation.getArgument(3);
                    return new RecommendationDto(userId, item.productId(), item.name(), "cat1", 
                            item.currentPrice().toString(), item.originalPrice().toString(), "0", 
                            item.averageRating().toString(), "10", item.availability(), reason, "now");
                });

        List<RecommendationResponse> results = recommendationService.getRecommendations(userId);

        assertThat(results).hasSize(1);
        List<RecommendationDto> recommendations = results.get(0).recommendations();
        
        // p1, p5, p6 should be included
        assertThat(recommendations).extracting(RecommendationDto::productId)
                .containsExactlyInAnyOrder("p1", "p5", "p6");

        // Verify reasons
        RecommendationDto p5Dto = recommendations.stream().filter(r -> r.productId().equals("p5")).findFirst().orElseThrow();
        assertThat(p5Dto.recommendationReason()).contains("Great deal");

        RecommendationDto p6Dto = recommendations.stream().filter(r -> r.productId().equals("p6")).findFirst().orElseThrow();
        assertThat(p6Dto.recommendationReason()).contains("Highly rated");

        RecommendationDto p1Dto = recommendations.stream().filter(r -> r.productId().equals("p1")).findFirst().orElseThrow();
        assertThat(p1Dto.recommendationReason()).contains("Matches your preferred category");
    }
}
