package pt.challenge.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.challenge.client.ExternalCatalogClient;
import pt.challenge.client.ExternalProfileClient;
import pt.challenge.dto.FeedbackRequest;
import pt.challenge.dto.FeedbackResponse;
import pt.challenge.dto.RecommendationDto;
import pt.challenge.dto.RecommendationResponse;
import pt.challenge.dto.external.product.ProductCatalogDto;
import pt.challenge.dto.external.product.ProductCatalogDto.ProductDto;
import pt.challenge.dto.external.user.UserProfileDto;
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
    private ExternalProfileClient externalProfileClient;
    @Mock
    private ExternalCatalogClient externalCatalogClient;
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
        UserProfileDto profile = UserProfileDto.builder()
                .userId(userId)
                .categories(Set.of("cat1"))
                .priceMin(10)
                .priceMax(100)
                .build();

        ProductDto p1 = ProductDto.builder().productId("p1").name("Item 1").currentPrice(50.0).originalPrice(50.0).averageRating(3.5).totalReviews("10").availability("IN_STOCK").build(); // Matches category (rating < 4.0)
        ProductDto p2 = ProductDto.builder().productId("p2").name("Item 2").currentPrice(5.0).originalPrice(5.0).averageRating(4.5).totalReviews("10").availability("IN_STOCK").build();  // Price low
        ProductDto p3 = ProductDto.builder().productId("p3").name("Item 3").currentPrice(150.0).originalPrice(150.0).averageRating(4.5).totalReviews("10").availability("IN_STOCK").build(); // Price high
        ProductDto p4 = ProductDto.builder().productId("p4").name("Item 4").currentPrice(50.0).originalPrice(50.0).averageRating(4.5).totalReviews("10").availability("OUT_OF_STOCK").build(); // Out of stock
        ProductDto p5 = ProductDto.builder().productId("p5").name("Item 5").currentPrice(40.0).originalPrice(60.0).averageRating(3.0).totalReviews("10").availability("IN_STOCK").build(); // Great deal (discount)
        ProductDto p6 = ProductDto.builder().productId("p6").name("Item 6").currentPrice(40.0).originalPrice(40.0).averageRating(4.2).totalReviews("10").availability("LOW_STOCK").build(); // Highly rated (rating >= 4.0)

        ProductCatalogDto cat1Response = ProductCatalogDto.builder()
                .category("cat1")
                .products(Set.of(p1, p2, p3, p4, p5, p6))
                .build();

        when(externalProfileClient.getUserProfile(userId)).thenReturn(profile);
        when(externalCatalogClient.getProductsByCategory("cat1")).thenReturn(cat1Response);
        
        when(recommendationMapper.toDto(eq(userId), eq("cat1"), any(), anyString()))
                .thenAnswer(invocation -> {
                    ProductDto item = invocation.getArgument(2);
                    String reason = invocation.getArgument(3);
                    return RecommendationDto.builder()
                            .userId(userId)
                            .productId(item.productId())
                            .productName(item.name())
                            .category("cat1")
                            .currentPrice(item.currentPrice().toString())
                            .originalPrice(item.originalPrice().toString())
                            .discount("0")
                            .averageRating(item.averageRating().toString())
                            .totalReviews("10")
                            .availability(item.availability())
                            .recommendationReason(reason)
                            .generatedAt("now")
                            .build();
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
