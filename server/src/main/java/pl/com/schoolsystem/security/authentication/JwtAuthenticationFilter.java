package pl.com.schoolsystem.security.authentication;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.com.schoolsystem.security.token.JWTService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JWTService JWTService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final var token = extractTokenFromRequest(request);
    token.ifPresent((this::authenticate));
    filterChain.doFilter(request, response);
  }

  private void authenticate(String token) {
    final var userEmail = JWTService.extractUserEmail(token);
  }

  private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .filter(header -> header.startsWith("Bearer "))
        .map(header -> header.replace("Bearer ", ""));
  }
}
