package pl.com.schoolsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.security.token.JWTService;
import pl.com.schoolsystem.security.user.ApplicationUserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;

  private final JWTService jwtService;

  private final ApplicationUserRepository applicationUserRepository;

  public String authenticate(AuthCommand command) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(command.getUsername(), command.getPassword()));
    final var user = applicationUserRepository.findByEmail(command.getUsername());
    return jwtService.generateToken(user.get());
  }
}
