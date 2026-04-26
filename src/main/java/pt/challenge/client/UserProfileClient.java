package pt.challenge.client;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pt.challenge.dto.external.user.UserProfileDto;
import pt.challenge.dto.external.user.UserProfileResponse;
import pt.challenge.mapper.UserProfileMapper;

/**
 * Client implementation for interacting with the External User Profile Service.
 * <p>
 * Utilizes Spring's RestClient for HTTP communication and Resilience4j for
 * resilience patterns like Retries and Bulkheads. Caching is applied to 
 * optimize performance for low-volatility profile data.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileClient implements ExternalProfileClient {

  private final RestClient userProfileRestClient;
  private final UserProfileMapper mapper;

  /**
   * Fetches the user profile from the external service.
   * <p>
   * This method is cached and protected by a bulkhead and a retry mechanism.
   * </p>
   *
   * @param userId the user ID to fetch the profile for
   * @return the mapped UserProfileDto
   */
  @Override
  @Cacheable(value = "userProfiles", key = "#userId")
  @Bulkhead(name = "userProfile")
  @Retry(name = "userProfile", fallbackMethod = "getUserProfileFallback")
  public UserProfileDto getUserProfile(String userId) {
    log.info("Requesting profile for user: {}", userId);
    UserProfileResponse userProfileResponse = userProfileRestClient.get()
        .uri("/api/users/{userId}/profile", userId)
        .retrieve()
        .body(UserProfileResponse.class);
    return mapper.toDto(userProfileResponse);
  }

  /**
   * Fallback method for getUserProfile when the service is unavailable or errors occur.
   * <p>
   * Returns a default UserProfileDto with empty preferences to ensure the 
   * recommendation flow can continue gracefully.
   * </p>
   *
   * @param userId the user ID
   * @param t the throwable cause of the failure
   * @return a default UserProfileDto
   */
  @Override
  public UserProfileDto getUserProfileFallback(String userId, Throwable t) {
    log.warn("Failed to fetch user profile for {}. Reason: {}. Using default fallback.", userId, t.getMessage());
    UserProfileResponse fallbackResponse = new UserProfileResponse(
        userId,
        new UserProfileResponse.Demographics(null, null, null, null, null),
        new UserProfileResponse.Preferences(java.util.Set.of(), new UserProfileResponse.PriceRange(0, 0), java.util.Set.of(), null),
        new UserProfileResponse.PurchaseHistory(0, 0.0, null, java.util.Set.of(), java.util.Map.of()),
        new UserProfileResponse.BehaviorPatterns(null, null, null, null)
    );
    return mapper.toDto(fallbackResponse);
  }
}
