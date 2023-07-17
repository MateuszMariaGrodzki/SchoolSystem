package pl.com.schoolsystem.headmaster;

import static pl.com.schoolsystem.headmaster.HeadmasterMapper.HEADMASTER_MAPPER;
import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;
import static pl.com.schoolsystem.security.user.PasswordGenerator.generatePassword;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeadmasterService {

  private final ApplicationUserService applicationUserService;

  private final HeadmasterRepository headmasterRepository;

  private final PasswordService passwordService;

  private final EmailSender emailSender;

  @Transactional
  public HeadmasterView create(HeadmasterCommand command) {
    final var password = generatePassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            command, passwordService.encodePassword(password), HEADMASTER);
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
}
