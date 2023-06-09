package pl.com.schoolsystem.security.configuration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static pl.com.schoolsystem.security.configuration.SecurityConstants.TOKEN_PREFIX;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.com.schoolsystem.security.token.JWTService;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public class EndpointLoggingFilter extends OncePerRequestFilter {

  private static final String REQUEST_LOGGING_PATTERN =
      "\n"
          + "---------------------------------------------------------"
          + "\nIncoming request\n"
          + "HttpMethod={}\n"
          + "URI={}\n"
          + "Content-Type={}\n"
          + "Headers={}\n"
          + "UserEmail={}\n"
          + "---------------------------------------------------------";

  private final JWTService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final var url = request.getRequestURL();
    final var httpMethod = request.getMethod();
    final var contentType = request.getContentType();
    final var token =
        Optional.ofNullable(request.getHeader(AUTHORIZATION))
            .filter(header -> header.startsWith(TOKEN_PREFIX))
            .map(header -> header.replace(TOKEN_PREFIX, ""))
            .orElse(null);
    final var userEmail = Optional.ofNullable(token).map(jwtService::extractUserEmail).orElse(null);
    final var headers = new ServletServerHttpRequest(request).getHeaders();

    log.info(REQUEST_LOGGING_PATTERN, httpMethod, url, contentType, headers, userEmail);
    filterChain.doFilter(request, response);
  }
}
