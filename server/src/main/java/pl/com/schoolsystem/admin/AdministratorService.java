package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.admin.AdministratorMapper.ADMINISTRATOR_MAPPER;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.EmailValidator;
import pl.com.schoolsystem.security.user.PasswordService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorService {

  private final ApplicationUserService applicationUserService;

  private final AdministratorRepository administratorRepository;

  private final PasswordService passwordService;

  private final EmailSender emailSender;

  private final EmailValidator emailValidator;

  @Transactional
  public AdministratorView create(AdministratorCommand command) {
    final var password = passwordService.generateNewRandomPassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            command.personalData(), passwordService.encodePassword(password), ADMIN);
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

  public AdministratorView getById(long id) {
    return administratorRepository
        .findById(id)
        .map(AdministratorEntity::getApplicationUser)
        .map(user -> ADMINISTRATOR_MAPPER.toAdministratorView(id, user))
        .orElseThrow(() -> new AdministratorNotFoundException(id));
  }

  @Transactional
  public AdministratorView updateById(long id, AdministratorCommand command) {
    final var administrator =
        administratorRepository
            .findById(id)
            .orElseThrow(() -> new AdministratorNotFoundException(id));
    final var applicationUser = administrator.getApplicationUser();
    if (emailValidator.isEmailUniqueInDatabase(applicationUser, command.personalData().email())) {
      final var personalData = command.personalData();
      applicationUser.setPhoneNumber(personalData.phoneNumber());
      applicationUser.setFirstName(personalData.firstName());
      applicationUser.setLastName(personalData.lastName());
      applicationUser.setEmail(personalData.email());
      log.info("Updated administrator with id {}", id);
      return ADMINISTRATOR_MAPPER.toAdministratorView(id, applicationUser);
    }
    throw new DuplicatedApplicationUserEmailException(command.personalData().email());
  }

  @Transactional
  public void deleteById(long id) {
    administratorRepository
        .findById(id)
        .map(AdministratorEntity::getApplicationUser)
        .ifPresent(
            user -> {
              user.setExpired(true);
              log.info("Set isExpired to administrator with id: {}", id);
            });
  }
}
