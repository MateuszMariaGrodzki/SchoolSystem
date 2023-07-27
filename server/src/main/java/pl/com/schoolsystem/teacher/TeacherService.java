package pl.com.schoolsystem.teacher;

import static pl.com.schoolsystem.security.user.ApplicationRole.TEACHER;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;
import static pl.com.schoolsystem.security.user.PasswordGenerator.generatePassword;
import static pl.com.schoolsystem.teacher.TeacherMapper.TEACHER_MAPPER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

  private final TeacherRepository teacherRepository;

  private final PasswordService passwordService;

  private final EmailSender emailSender;

  private final ApplicationUserService applicationUserService;

  @Transactional
  public TeacherView create(TeacherCommand teacherCommand) {
    final var password = generatePassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            teacherCommand, passwordService.encodePassword(password), TEACHER);
    final var applicationUserEntity = applicationUserService.create(applicationUserCommand);
    final var teacherEntity = TEACHER_MAPPER.toTeacherEntity(applicationUserEntity);
    final var savedEntity = teacherRepository.save(teacherEntity);
    final var teacherId = savedEntity.getId();
    log.info(
        "Created new teacher with email: {} and id: {}",
        applicationUserEntity.getEmail(),
        teacherId);
    emailSender.sendNewUserEmail(applicationUserEntity, password);
    return TEACHER_MAPPER.toTeacherView(teacherId, applicationUserEntity);
  }

  public TeacherView getById(long id) {
    return teacherRepository
        .findById(id)
        .map(TeacherEntity::getApplicationUser)
        .map(user -> TEACHER_MAPPER.toTeacherView(id, user))
        .orElseThrow(() -> new TeacherNotFoundException(id));
  }

  @Transactional
  public TeacherView updateById(long id, TeacherCommand command) {
    final var teacher =
        teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException(id));
    final var applicationUser = teacher.getApplicationUser();
    if (isEmailValid(applicationUser, command.email())) {
      applicationUser.setPhoneNumber(command.phoneNumber());
      applicationUser.setFirstName(command.firstName());
      applicationUser.setLastName(command.lastName());
      applicationUser.setEmail(command.email());
      log.info("Updated teacher with id {}", id);
      return TEACHER_MAPPER.toTeacherView(id, applicationUser);
    }
    throw new DuplicatedApplicationUserEmailException(command.email());
  }

  private boolean isEmailValid(ApplicationUserEntity applicationUser, String email) {
    if (!isEmailFromRequestEqualToEmailFromDatabase(email, applicationUser.getEmail())) {
      return !applicationUserService.existsByEmail(email);
    }
    return true;
  }

  private boolean isEmailFromRequestEqualToEmailFromDatabase(
      String requestEmail, String databaseEmail) {
    return requestEmail.equals(databaseEmail);
  }

  @Transactional
  public void deleteById(long id) {
    teacherRepository
        .findById(id)
        .map(TeacherEntity::getApplicationUser)
        .ifPresent(
            user -> {
              log.info("Deleting teacher with id {}", id);
              user.setExpired(true);
            });
  }
}
