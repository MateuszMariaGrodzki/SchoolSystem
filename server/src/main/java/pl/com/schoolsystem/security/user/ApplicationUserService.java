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

  public ApplicationUserEntity create(AddApplicationUserCommand command) {
    final var entity =
        ApplicationUserMapper.APPLICATION_USER_MAPPER.toApplicationUserEntity(command);
    final var x = applicationUserRepository.save(entity);
    return x;
  }
}
