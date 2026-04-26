package pt.challenge.client;

import pt.challenge.dto.external.user.UserProfileDto;

/**
 * Interface defining the contract for the External Profile Client.
 * <p>
 * This client is responsible for fetching user-specific profile data,
 * including preferences and demographics, from an external service.
 * </p>
 */
public interface ExternalProfileClient {

  /**
   * Fetches the profile for a given user.
   *
   * @param userId the unique identifier of the user
   * @return the user profile DTO
   */
  UserProfileDto getUserProfile(String userId);

  /**
   * Fallback method used when the user profile service is unavailable.
   *
   * @param userId the unique identifier of the user
   * @param t the exception that triggered the fallback
   * @return a default user profile DTO
   */
  UserProfileDto getUserProfileFallback(String userId, Throwable t);

}
