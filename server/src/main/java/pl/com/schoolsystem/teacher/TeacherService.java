package pl.com.schoolsystem.teacher;

import static pl.com.schoolsystem.security.user.ApplicationRole.TEACHER;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;
import static pl.com.schoolsystem.teacher.TeacherMapper.TEACHER_MAPPER;
import static pl.com.schoolsystem.teacher.TeacherSpecification.*;

import java.util.Optional;
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
public class TeacherService {

  private final TeacherRepository teacherRepository;

  private final PasswordService passwordService;

  private final EmailSender emailSender;

  private final ApplicationUserService applicationUserService;

  private final EmailValidator emailValidator;

  @Transactional
  public TeacherView create(CreateTeacherCommand teacherCommand) {
    final var password = passwordService.generateNewRandomPassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            teacherCommand.personalData(), passwordService.encodePassword(password), TEACHER);
    final var applicationUserEntity = applicationUserService.create(applicationUserCommand);
    final var teacherEntity =
        TEACHER_MAPPER.toTeacherEntity(applicationUserEntity, teacherCommand.specialization());
    final var savedEntity = teacherRepository.save(teacherEntity);
    final var teacherId = savedEntity.getId();
    log.info(
        "Created new teacher with email: {} and id: {}",
        applicationUserEntity.getEmail(),
        teacherId);
    emailSender.sendNewUserEmail(applicationUserEntity, password);
    return TEACHER_MAPPER.toTeacherView(
        teacherId, applicationUserEntity, teacherCommand.specialization());
  }

  public TeacherView getById(long id) {
    return teacherRepository
        .findOne(withId(id).and(isAccountActive()))
        .map(
            teacher ->
                TEACHER_MAPPER.toTeacherView(
                    id, teacher.getApplicationUser(), teacher.getSpecialization()))
        .orElseThrow(() -> new TeacherNotFoundException(id));
  }

  @Transactional
  public TeacherView updateById(long id, UpdateTeacherCommand command) {
    final var teacher =
        teacherRepository
            .findOne(withId(id).and(isAccountActive()))
            .orElseThrow(() -> new TeacherNotFoundException(id));
    final var applicationUser = teacher.getApplicationUser();
    if (emailValidator.isEmailUniqueInDatabase(applicationUser, command.personalData().email())) {
      final var personalData = command.personalData();
      applicationUser.setPhoneNumber(personalData.phoneNumber());
      applicationUser.setFirstName(personalData.firstName());
      applicationUser.setLastName(personalData.lastName());
      applicationUser.setEmail(personalData.email());
      log.info("Updated teacher with id {}", id);
      return TEACHER_MAPPER.toTeacherView(id, applicationUser, teacher.getSpecialization());
    }
    throw new DuplicatedApplicationUserEmailException(command.personalData().email());
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

  public Optional<TeacherEntity> findByIdAndLoggedUser(long id) {
    final var loggedUserId = applicationUserService.getAuthenticatedUserId();
    return teacherRepository.findOne(
        withId(id).and(isAccountActive()).and(isLoggedUser(loggedUserId)));
  }
}
