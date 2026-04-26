package pt.challenge.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import pt.challenge.dto.external.product.ProductCatalogDto;
import pt.challenge.dto.external.product.ProductCatalogDto.ProductDto;
import pt.challenge.dto.external.product.ProductCategoryResponse;

/**
 * Mapper for converting External Product Catalog responses to internal DTOs.
 * <p>
 * Centralizes the transformation logic to keep clients focused on communication.
 * </p>
 */
@Component
public class ProductCatalogMapper {

  /**
   * Maps a ProductCategoryResponse from the external service to a ProductCatalogDto.
   *
   * @param response the raw response from the external catalog service
   * @return the mapped ProductCatalogDto
   */
  public ProductCatalogDto toDto(ProductCategoryResponse response) {
    if (response == null) {
      return null;
    }

    Set<ProductDto> products = response.products().stream().map(
            productItemResponse -> ProductDto.builder()
                .productId(productItemResponse.productId())
                .name(productItemResponse.name())
                .currentPrice(productItemResponse.currentPrice())
                .originalPrice(productItemResponse.originalPrice())
                .averageRating(productItemResponse.averageRating())
                .availability(productItemResponse.availability())
                .totalReviews(String.valueOf(productItemResponse.totalReviews()))
                .build())
        .collect(Collectors.toSet());

    return ProductCatalogDto.builder()
        .category(response.category())
        .products(products)
        .build();
  }
}
