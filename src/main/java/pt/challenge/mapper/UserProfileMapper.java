package pt.challenge.mapper;

import org.springframework.stereotype.Component;
import pt.challenge.dto.external.user.UserProfileDto;
import pt.challenge.dto.external.user.UserProfileResponse;

/**
 * Mapper for converting External User Profile responses to internal DTOs.
 */
@Component
public class UserProfileMapper {

  /**
   * Maps a UserProfileResponse to a UserProfileDto.
   *
   * @param response the raw user profile response from the external service
   * @return the mapped UserProfileDto, or null if response is null
   */
  public UserProfileDto toDto(UserProfileResponse response) {
    if (response == null) {
      return null;
    }

    return UserProfileDto.builder()
        .userId(response.userId())
        .categories(response.preferences().categories())
        .priceMax(response.preferences().priceRange().max())
        .priceMin(response.preferences().priceRange().min())
        .build();
  }

}
