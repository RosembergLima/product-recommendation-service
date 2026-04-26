package pt.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pt.challenge.client.ProductCatalogClient;
import pt.challenge.client.UserProfileClient;
import pt.challenge.dto.RecommendationResponse;
import pt.challenge.dto.external.product.ProductCategoryResponse;
import pt.challenge.dto.external.user.UserProfileResponse;
import pt.challenge.service.RecommendationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ProductRecommendationServiceApplicationTests {

  @Autowired
  private RecommendationService recommendationService;

  @MockitoBean
  private UserProfileClient userProfileClient;

  @MockitoBean
  private ProductCatalogClient productCatalogClient;

  @Test
  void shouldGetRecommendations() {
    String userId = "123";

    UserProfileResponse profile = new UserProfileResponse(
        userId,
        null,
        new UserProfileResponse.Preferences(Set.of("electronics"), new UserProfileResponse.PriceRange(0, 1000), Set.of(), "style"),
        null,
        null
    );

    ProductCategoryResponse categoryResponse = new ProductCategoryResponse(
        "electronics",
        1,
        Set.of(new ProductCategoryResponse.ProductItem("PROD-1", "Test Product", 100.0, 150.0, 4.5, 10, "IN_STOCK")),
        null
    );

    when(userProfileClient.getUserProfile(userId)).thenReturn(profile);
    when(productCatalogClient.getProductsByCategory("electronics")).thenReturn(categoryResponse);

    List<RecommendationResponse> responses = recommendationService.getRecommendations(userId);

    assertThat(responses).isNotEmpty();
    assertThat(responses.get(0).totalRecommendations()).isEqualTo((short) 1);
    assertThat(responses.get(0).recommendations().get(0).userId()).isEqualTo(userId);
  }

}
