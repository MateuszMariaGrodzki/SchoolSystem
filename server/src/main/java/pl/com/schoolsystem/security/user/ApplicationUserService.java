package pl.com.schoolsystem.security.user;

import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;

import io.vavr.control.Either;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationUserService {

  private final ApplicationUserRepository applicationUserRepository;

  private final PasswordService passwordService;

  private final AuthenticationFacade authenticationFacade;

  public ApplicationUserEntity getByEmailsOrElseThrowApplicationUserNotFoundException(
      String email) {
    return applicationUserRepository
        .findByEmail(email)
        .orElseThrow(() -> new ApplicationUserNotFoundException(email));
  }

  public boolean existsByEmail(String email) {
    return applicationUserRepository.existsByEmail(email);
  }

  public ApplicationUserEntity create(AddApplicationUserCommand command) {
    if (applicationUserRepository.existsByEmail(command.email())) {
      throw new DuplicatedApplicationUserEmailException(command.email());
    }
    final var entity = APPLICATION_USER_MAPPER.toApplicationUserEntity(command);
    final var savedEntity = applicationUserRepository.save(entity);
    log.info(
        "Created new application user with email {} and id {}",
        savedEntity.getEmail(),
        savedEntity.getId());
    return savedEntity;
  }

  @Transactional
  public Either<Map<String, String>, Void> changePassword(ChangePasswordCommand command) {
    final var applicationUser = authenticationFacade.getAuthenticatedUser();
    final var eitherValidationErrorsOrEncryptedPassword =
        passwordService.changePassword(command, applicationUser);

    return eitherValidationErrorsOrEncryptedPassword
        .map(password -> changePassword(applicationUser.getEmail(), password))
        .orElse(() -> Either.left(eitherValidationErrorsOrEncryptedPassword.getLeft()));
  }

  private Void changePassword(String email, String password) {
    applicationUserRepository
        .findByEmail(email)
        .ifPresentOrElse(
            user -> {
              user.setPassword(password);
              log.info("Changing password for user with email {}", email);
            },
            () -> {
              throw new ApplicationUserNotFoundException(email);
            });
    return null;
  }
}
