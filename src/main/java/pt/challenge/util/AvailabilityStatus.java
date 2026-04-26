package pt.challenge.util;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing the possible availability statuses of a product.
 */
public enum AvailabilityStatus {
    /**
     * Product is currently in stock.
     */
    IN_STOCK,
    /**
     * Product has low stock but is still available for recommendation.
     */
    LOW_STOCK;

    /**
     * Returns a list of status names that are considered "available" for recommendations.
     *
     * @return list of available status names
     */
    public static List<String> getAvailableStatuses() {
        return Arrays.stream(values())
                .map(Enum::name)
                .toList();
    }
}
