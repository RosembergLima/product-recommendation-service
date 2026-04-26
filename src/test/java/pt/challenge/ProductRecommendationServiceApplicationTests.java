package pt.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pt.challenge.client.ExternalCatalogClient;
import pt.challenge.client.ExternalProfileClient;
import pt.challenge.dto.RecommendationResponse;
import pt.challenge.dto.external.product.ProductCatalogDto;
import pt.challenge.dto.external.product.ProductCatalogDto.ProductDto;
import pt.challenge.dto.external.user.UserProfileDto;
import pt.challenge.service.RecommendationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ProductRecommendationServiceApplicationTests {

  @Autowired
  private RecommendationService recommendationService;

  @MockitoBean
  private ExternalProfileClient externalProfileClient;

  @MockitoBean
  private ExternalCatalogClient externalCatalogClient;

  @Test
  void shouldGetRecommendations() {
    String userId = "123";

    UserProfileDto profile = UserProfileDto.builder()
        .userId(userId)
        .categories(Set.of("electronics"))
        .priceMin(0)
        .priceMax(1000)
        .build();

    ProductCatalogDto categoryResponse = ProductCatalogDto.builder()
        .category("electronics")
        .products(Set.of(ProductDto.builder()
            .productId("PROD-1")
            .name("Test Product")
            .currentPrice(100.0)
            .originalPrice(150.0)
            .averageRating(4.5)
            .totalReviews("10")
            .availability("IN_STOCK")
            .build()))
        .build();

    when(externalProfileClient.getUserProfile(userId)).thenReturn(profile);
    when(externalCatalogClient.getProductsByCategory("electronics")).thenReturn(categoryResponse);

    List<RecommendationResponse> responses = recommendationService.getRecommendations(userId);

    assertThat(responses).isNotEmpty();
    assertThat(responses.get(0).totalRecommendations()).isEqualTo(1);
    assertThat(responses.get(0).recommendations().get(0).userId()).isEqualTo(userId);
  }

}
