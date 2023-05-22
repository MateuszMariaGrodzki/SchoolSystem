package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.admin.AdministratorMapper.ADMINISTRATOR_MAPPER;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;
import static pl.com.schoolsystem.security.user.PasswordGenerator.generatePassword;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorService {

  private final ApplicationUserService applicationUserService;

  private final AdministratorRepository administratorRepository;

  private final PasswordEncoder passwordEncoder;

  private final EmailSender emailSender;

  @Transactional
  public AddAdministratorView create(AddAdministratorCommand command) {
    final var password = generatePassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            command, passwordEncoder.encode(password), ADMIN);
    final var applicationUserEntity = applicationUserService.create(applicationUserCommand);
    final var administratorEntity =
        ADMINISTRATOR_MAPPER.toAdministratorEntity(applicationUserEntity);
    final var savedEntity = administratorRepository.save(administratorEntity);
    final var administratorId = savedEntity.getId();
    log.info(
        "Created new administrator with email: {} and id: {}",
        applicationUserEntity.getEmail(),
        administratorId);
    emailSender.sendNewUserEmail(applicationUserEntity, password);
    return ADMINISTRATOR_MAPPER.toAdministratorView(administratorId, applicationUserEntity);
  }
}
