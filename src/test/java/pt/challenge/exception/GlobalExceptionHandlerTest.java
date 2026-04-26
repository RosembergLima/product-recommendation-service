package pt.challenge.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "field", "error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(((Map) response.getBody().get("errors")).get("field")).isEqualTo("error message");
    }

    @Test
    void shouldHandleConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException("violation", Set.of());
        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolationException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleNotFound() {
        HttpClientErrorException.NotFound ex = (HttpClientErrorException.NotFound) HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldHandleCallNotPermitted() {
        CircuitBreaker cb = mock(CircuitBreaker.class);
        CircuitBreakerConfig config = mock(CircuitBreakerConfig.class);
        when(cb.getName()).thenReturn("testCB");
        when(cb.getCircuitBreakerConfig()).thenReturn(config);
        when(config.isWritableStackTraceEnabled()).thenReturn(true);
        
        CallNotPermittedException ex = CallNotPermittedException.createCallNotPermittedException(cb);
        
        ResponseEntity<Map<String, Object>> response = handler.handleCallNotPermittedException(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().get("message")).isEqualTo("External service is temporarily unavailable");
    }

    @Test
    void shouldHandleGeneralException() {
        Exception ex = new RuntimeException("Unexpected");
        ResponseEntity<Map<String, Object>> response = handler.handleGeneralException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
    }
}
