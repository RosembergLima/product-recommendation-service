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

@Component
public class CorrelationIdFilter implements Filter {

  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  private static final String MDC_KEY = "correlationId";

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