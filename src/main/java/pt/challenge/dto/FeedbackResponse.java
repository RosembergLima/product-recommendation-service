package pt.challenge.dto;

/**
 * Data Transfer Object for feedback responses.
 *
 * @param status the status of the operation (e.g., "success")
 * @param message a descriptive message about the result
 */
public record FeedbackResponse(String status, String message) {

}
