package pt.challenge.dto;

import jakarta.validation.constraints.NotNull;
import pt.challenge.util.FeedbackType;

public record FeedbackRequest(
    @NotNull String userId,
    @NotNull String productId,
    @NotNull FeedbackType feedback,
    String comment) {

}
