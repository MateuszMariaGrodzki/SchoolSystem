package pl.com.schoolsystem.security.user;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.com.schoolsystem.common.exception.UnexpectedBehaviourException;

@Component
public class AuthenticationFacade {

  public ApplicationUserEntity getAuthenticatedUser() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .filter(ApplicationUserEntity.class::isInstance)
        .map(ApplicationUserEntity.class::cast)
        .orElseThrow(
            () ->
                new UnexpectedBehaviourException(
                    "USER_NOT_IN_SECURITY_CONTEXT", "Nie znaleziono użytkownika w kontekście"));
  }

  public long getAuthenticatedUserId() {
    return getAuthenticatedUser().getId();
  }
}
