package pt.challenge.mapper;

import org.junit.jupiter.api.Test;
import pt.challenge.dto.RecommendationDto;
import pt.challenge.dto.external.product.ProductCategoryResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationMapperTest {

    private final RecommendationMapper mapper = new RecommendationMapper();

    @Test
    void shouldMapToDto() {
        String userId = "user1";
        String category = "electronics";
        ProductCategoryResponse.ProductItem product = new ProductCategoryResponse.ProductItem(
                "p1", "Phone", 900.0, 1000.0, 4.8, 100, "IN_STOCK"
        );
        String reason = "Great deal";

        RecommendationDto dto = mapper.toDto(userId, category, product, reason);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.productId()).isEqualTo("p1");
        assertThat(dto.productName()).isEqualTo("Phone");
        assertThat(dto.category()).isEqualTo(category);
        assertThat(dto.currentPrice()).isEqualTo("900.0");
        assertThat(dto.originalPrice()).isEqualTo("1000.0");
        assertThat(dto.discount()).isEqualTo("100.00");
        assertThat(dto.averageRating()).isEqualTo("4.8");
        assertThat(dto.totalReviews()).isEqualTo("100");
        assertThat(dto.availability()).isEqualTo("IN_STOCK");
        assertThat(dto.recommendationReason()).isEqualTo(reason);
        assertThat(dto.generatedAt()).isNotNull();
    }
}
