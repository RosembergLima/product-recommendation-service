package pt.challenge.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles validation exceptions for @RequestBody @Valid parameters.
   *
   * @param ex the exception
   * @return error response with validation details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.debug("Validation failed for request: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  /**
   * Handles constraint violations for @PathVariable or @RequestParam.
   *
   * @param ex the exception
   * @return error response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.debug("Constraint violation: {}", ex.getMessage());
    return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Handles 404 Not Found errors from external service calls.
   *
   * @param ex the exception
   * @return error response
   */
  @ExceptionHandler(HttpClientErrorException.NotFound.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(HttpClientErrorException.NotFound ex) {
    log.error("External resource not found. Status: {}, Message: {}", ex.getStatusCode(), ex.getMessage());
    return createErrorResponse(HttpStatus.NOT_FOUND, "Resource not found");
  }

  /**
   * Handles cases where the circuit breaker prevents a call to an external service.
   *
   * @param ex the exception
   * @return error response with service unavailable status
   */
  @ExceptionHandler(CallNotPermittedException.class)
  public ResponseEntity<Map<String, Object>> handleCallNotPermittedException(
      CallNotPermittedException ex) {
    log.error("Circuit breaker is open. Blocking call: {}", ex.getMessage());
    return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
        "External service is temporarily unavailable");
  }

  /**
   * Fallback for all other unhandled exceptions.
   *
   * @param ex the exception
   * @return internal server error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
    log.error("Unhandled internal server error: ", ex);
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
  }

  /**
   * Utility method to create a standardized error response body.
   *
   * @param status the HTTP status
   * @param message the error message
   * @return the response entity
   */
  private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status,
      String message) {
    return createErrorResponse(status, message, null);
  }

  /**
   * Utility method to create a standardized error response body with validation errors.
   *
   * @param status the HTTP status
   * @param message the error message
   * @param errors map of validation errors
   * @return the response entity
   */
  private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status,
      String message, Map<String, String> errors) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    if (errors != null) {
      body.put("errors", errors);
    }
    return new ResponseEntity<>(body, status);
  }
}
