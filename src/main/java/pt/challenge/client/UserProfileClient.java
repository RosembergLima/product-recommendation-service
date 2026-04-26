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
import pt.challenge.dto.external.user.UserProfileResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileClient {

  private final RestClient userProfileRestClient;

  /**
   * Fetches the user profile from the external service.
   *
   * @param userId the user ID
   * @return the user profile response
   */
  @Cacheable(value = "userProfiles", key = "#userId")
  @Bulkhead(name = "userProfile")
  @Retry(name = "userProfile", fallbackMethod = "getUserProfileFallback")
  public UserProfileResponse getUserProfile(String userId) {
    log.info("Requesting profile for user: {}", userId);
    return userProfileRestClient.get()
        .uri("/api/users/{userId}/profile", userId)
        .retrieve()
        .body(UserProfileResponse.class);
  }

  /**
   * Fallback method for getUserProfile when the service is unavailable or errors occur.
   *
   * @param userId the user ID
   * @param t the throwable cause
   * @return a default user profile
   */
  public UserProfileResponse getUserProfileFallback(String userId, Throwable t) {
    log.error("Failed to fetch user profile for {}. Reason: {}. Using default fallback.", userId, t.getMessage(), t);
    return new UserProfileResponse(
        userId,
        new UserProfileResponse.Demographics(null, null, null, null, null),
        new UserProfileResponse.Preferences(Set.of(), null, Set.of(), null),
        new UserProfileResponse.PurchaseHistory(0, 0.0, null, Set.of(), Map.of()),
        new UserProfileResponse.BehaviorPatterns(null, null, null, null)
    );
  }
}
