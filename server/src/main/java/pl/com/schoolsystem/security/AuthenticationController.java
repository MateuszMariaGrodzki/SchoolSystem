package pl.com.schoolsystem.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static pl.com.schoolsystem.security.configuration.SecurityConstants.TOKEN_ENDPOINT;
import static pl.com.schoolsystem.security.configuration.SecurityConstants.TOKEN_PREFIX;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@PreAuthorize("permitAll()")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping(TOKEN_ENDPOINT)
  public ResponseEntity<Void> token(@RequestBody AuthCommand command) {
    final var token = authenticationService.authenticate(command);
    final var headers = new HttpHeaders();
    headers.add(AUTHORIZATION, TOKEN_PREFIX + token);
    return ResponseEntity.noContent().headers(headers).build();
  }

  @GetMapping("/info")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public String get() {
    return "okv2";
  }
}
