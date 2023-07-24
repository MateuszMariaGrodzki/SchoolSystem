package pl.com.schoolsystem.teacher;

import static pl.com.schoolsystem.security.user.ApplicationRole.TEACHER;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;
import static pl.com.schoolsystem.security.user.PasswordGenerator.generatePassword;
import static pl.com.schoolsystem.teacher.TeacherMapper.TEACHER_MAPPER;

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
}
