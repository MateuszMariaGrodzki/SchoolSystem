package pl.com.schoolsystem.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.security.token.JWTService;
import pl.com.schoolsystem.security.user.ApplicationUserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;

  private final JWTService jwtService;

  private final ApplicationUserService applicationUserService;

  public String authenticate(AuthCommand command) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(command.email(), command.password()));
    final var user =
        applicationUserService.getByEmailsOrElseThrowApplicationUserNotFoundException(
            command.email());
    return jwtService.generateToken(user);
  }
}
