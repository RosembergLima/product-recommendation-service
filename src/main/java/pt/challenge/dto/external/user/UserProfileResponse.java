package pt.challenge.dto.external.user;

import java.util.Map;
import java.util.Set;

public record UserProfileResponse(
    String userId,
    Demographics demographics,
    Preferences preferences,
    PurchaseHistory purchaseHistory,
    BehaviorPatterns behaviorPatterns
) {

  public record Demographics(
      Integer age,
      String gender,
      String location,
      String income,
      String education
  ) {

  }

  public record Preferences(
      Set<String> categories,
      PriceRange priceRange,
      Set<String> brands,
      String style
  ) {

  }

  public record PriceRange(
      Integer min,
      Integer max
  ) {

  }

  public record PurchaseHistory(
      Integer totalOrders,
      Double averageOrderValue,
      String lastPurchaseDate,
      Set<String> favoriteCategories,
      Map<String, Integer> seasonalPatterns
  ) {

  }

  public record BehaviorPatterns(
      String browsingFrequency,
      String purchaseFrequency,
      String priceSensitivity,
      String brandLoyalty
  ) {

  }
}
