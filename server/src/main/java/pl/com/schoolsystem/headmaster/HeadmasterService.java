package pl.com.schoolsystem.headmaster;

import static pl.com.schoolsystem.headmaster.HeadmasterMapper.HEADMASTER_MAPPER;
import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;
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
public class HeadmasterService {

  private final ApplicationUserService applicationUserService;

  private final HeadmasterRepository headmasterRepository;

  private final PasswordService passwordService;

  private final EmailSender emailSender;

  private final EmailValidator emailValidator;

  @Transactional
  public HeadmasterView create(HeadmasterCommand command) {
    final var password = passwordService.generateNewRandomPassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            command.personalData(), passwordService.encodePassword(password), HEADMASTER);
    final var applicationUserEntity = applicationUserService.create(applicationUserCommand);

    final var headmasterEntity = HEADMASTER_MAPPER.toHeadmasterEntity(applicationUserEntity);

    final var savedEntity = headmasterRepository.save(headmasterEntity);
    final var headmasterId = savedEntity.getId();
    log.info(
        "Created new headmaster with email: {} and id: {}",
        applicationUserEntity.getEmail(),
        headmasterId);
    emailSender.sendNewUserEmail(applicationUserEntity, password);
    return HEADMASTER_MAPPER.toHeadmasterView(headmasterId, applicationUserEntity);
  }

  public HeadmasterView getById(long id) {
    return headmasterRepository
        .findById(id)
        .map(HeadmasterEntity::getApplicationUser)
        .map(user -> HEADMASTER_MAPPER.toHeadmasterView(id, user))
        .orElseThrow(() -> new HeadmasterNotFoundException(id));
  }

  @Transactional
  public HeadmasterView updateById(long id, HeadmasterCommand command) {
    final var headmaster =
        headmasterRepository.findById(id).orElseThrow(() -> new HeadmasterNotFoundException(id));
    final var applicationUser = headmaster.getApplicationUser();
    if (emailValidator.isEmailUniqueInDatabase(applicationUser, command.personalData().email())) {
      final var personalData = command.personalData();
      applicationUser.setPhoneNumber(personalData.phoneNumber());
      applicationUser.setFirstName(personalData.firstName());
      applicationUser.setLastName(personalData.lastName());
      applicationUser.setEmail(personalData.email());
      log.info("Updated headmaster with id {}", id);
      return HEADMASTER_MAPPER.toHeadmasterView(id, applicationUser);
    }
    throw new DuplicatedApplicationUserEmailException(command.personalData().email());
  }

  @Transactional
  public void deleteById(long id) {
    headmasterRepository
        .findById(id)
        .map(HeadmasterEntity::getApplicationUser)
        .ifPresent(
            user -> {
              user.setExpired(true);
              log.info("Set isExpired to headmaster with id: {}", id);
            });
  }
}
