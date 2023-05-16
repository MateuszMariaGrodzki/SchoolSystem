package pl.com.schoolsystem.security.user;

import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationUserService {

  private final ApplicationUserRepository applicationUserRepository;

  public ApplicationUserEntity getByEmailsOrElseThrowApplicationUserNotFoundException(
      String email) {
    return applicationUserRepository
        .findByEmail(email)
        .orElseThrow(() -> new ApplicationUserNotFoundException(email));
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
}
