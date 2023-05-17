package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.admin.AdministratorMapper.ADMINISTRATOR_MAPPER;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.security.user.ApplicationUserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorService {

  private final ApplicationUserService applicationUserService;

  private final AdministratorRepository administratorRepository;

  public AddAdministratorView create(AddAdministratorCommand command) {
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            command, UUID.randomUUID().toString(), ADMIN);
    final var applicationUserEntity = applicationUserService.create(applicationUserCommand);
    final var administratorEntity =
        ADMINISTRATOR_MAPPER.toAdministratorEntity(applicationUserEntity);
    final var savedEntity = administratorRepository.save(administratorEntity);
    final var administratorId = savedEntity.getId();
    log.info(
        "Created new administrator with email: {} and id: {}",
        applicationUserEntity.getEmail(),
        administratorId);
    return ADMINISTRATOR_MAPPER.toAdministratorView(administratorId, applicationUserEntity);
  }
}
