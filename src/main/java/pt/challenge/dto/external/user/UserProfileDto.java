package pt.challenge.dto.external.user;

import java.util.Set;
import lombok.Builder;

/**
 * Data Transfer Object for internal user profile representation.
 *
 * @param userId the unique identifier of the user
 * @param categories the set of preferred categories
 * @param priceMax the maximum preferred price
 * @param priceMin the minimum preferred price
 */
@Builder
public record UserProfileDto(String userId, Set<String> categories, Integer priceMax, Integer priceMin) {

}
