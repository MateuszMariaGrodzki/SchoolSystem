package pl.com.schoolsystem.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;

@Service
@RequiredArgsConstructor
public class ApplicationUserService {

  private final ApplicationUserRepository applicationUserRepository;

  public ApplicationUserEntity getByEmailsOrElseThrowApplicationUserNotFoundException(
      String email) {
    return applicationUserRepository
        .findByEmail(email)
        .orElseThrow(() -> new ApplicationUserNotFoundException(email));
  }
}
