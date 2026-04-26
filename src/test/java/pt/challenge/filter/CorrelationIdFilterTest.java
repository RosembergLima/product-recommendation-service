package pt.challenge.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldAddCorrelationIdToMdcFromHeader() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        when(request.getHeader("X-Correlation-Id")).thenReturn("test-id");

        filter.doFilter(request, response, (req, res) -> {
            assertThat(MDC.get("correlationId")).isEqualTo("test-id");
        });

        assertThat(MDC.get("correlationId")).isNull();
        verify(request, atLeastOnce()).getHeader("X-Correlation-Id");
    }

    @Test
    void shouldSkipIfRequestNotHttpServletRequest() throws ServletException, IOException {
        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldHandleBlankCorrelationIdHeader() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader("X-Correlation-Id")).thenReturn("  ");

        filter.doFilter(request, response, (req, res) -> {
            assertThat(MDC.get("correlationId")).isNotNull().isNotEmpty();
            assertThat(MDC.get("correlationId")).isNotEqualTo("  ");
        });

        assertThat(MDC.get("correlationId")).isNull();
    }
}
