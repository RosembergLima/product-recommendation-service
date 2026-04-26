package pt.challenge.dto;

import jakarta.validation.constraints.NotNull;
import pt.challenge.util.FeedbackType;

/**
 * Data Transfer Object for user feedback requests.
 *
 * @param userId the ID of the user providing feedback
 * @param productId the ID of the product being reviewed
 * @param feedback the type of feedback (e.g., LIKED, DISLIKED)
 * @param comment optional additional comments from the user
 */
public record FeedbackRequest(
    @NotNull String userId,
    @NotNull String productId,
    @NotNull FeedbackType feedback,
    String comment) {

}
