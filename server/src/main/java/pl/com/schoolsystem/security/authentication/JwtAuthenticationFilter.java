package pl.com.schoolsystem.security.authentication;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static pl.com.schoolsystem.security.configuration.SecurityConstants.TOKEN_PREFIX;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.com.schoolsystem.security.token.JWTService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JWTService jWTService;

  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final var token = extractTokenFromRequest(request);
    token.ifPresent(token2 -> authenticate(token2, request));
    filterChain.doFilter(request, response);
  }

  private void authenticate(String token, HttpServletRequest request) {
    final var userEmail = jWTService.extractUserEmail(token);
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
      if (jWTService.isTokenValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
  }

  private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .filter(header -> header.startsWith(TOKEN_PREFIX))
        .map(header -> header.replace(TOKEN_PREFIX, ""));
  }
}
