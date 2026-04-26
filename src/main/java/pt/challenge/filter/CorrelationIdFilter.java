package pt.challenge.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Filter responsible for managing the Correlation ID for distributed tracing.
 * <p>
 * This filter extracts the {@code X-Correlation-Id} header from incoming requests.
 * If the header is missing, it generates a new UUID. The ID is then stored in the
 * SLF4J MDC (Mapped Diagnostic Context) to ensure all subsequent log messages
 * for the current request include this ID.
 * </p>
 */
@Component
public class CorrelationIdFilter implements Filter {

  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  private static final String MDC_KEY = "correlationId";

  /**
   * Filters every HTTP request to manage the Correlation ID in the MDC.
   *
   * @param request  the {@link ServletRequest} object contains the client's request
   * @param response the {@link ServletResponse} object contains the filter's response
   * @param chain    the {@link FilterChain} for invoking the next filter in the chain
   * @throws IOException      if an I/O error occurs during the processing
   * @throws ServletException if the processing fails for any other reason
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest httpServletRequest) {
      String correlationId = httpServletRequest.getHeader(CORRELATION_ID_HEADER);
      if (correlationId == null || correlationId.isBlank()) {
        correlationId = UUID.randomUUID().toString();
      }
      MDC.put(MDC_KEY, correlationId);
    }

    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}