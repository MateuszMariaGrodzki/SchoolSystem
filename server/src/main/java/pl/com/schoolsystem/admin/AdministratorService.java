package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.security.user.ApplicationUserMapper;
import pl.com.schoolsystem.security.user.ApplicationUserService;

@Service
@RequiredArgsConstructor
public class AdministratorService {

  private final ApplicationUserService applicationUserService;

  private final AdministratorRepository administratorRepository;

  public AddAdministratorView create(AddAdministratorCommand command) {
    final var applicationUserCommand =
        ApplicationUserMapper.APPLICATION_USER_MAPPER.toApplicationUserCommand(
            command, UUID.randomUUID().toString(), ADMIN);
    final var entity = applicationUserService.create(applicationUserCommand);
    AdministratorEntity administratorEntity = new AdministratorEntity();
    administratorEntity.assignApplicationUser(entity);
    administratorRepository.save(administratorEntity);
    return ApplicationUserMapper.APPLICATION_USER_MAPPER.toAdministratorView(entity);
  }
}
