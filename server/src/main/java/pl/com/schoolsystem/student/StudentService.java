package pl.com.schoolsystem.student;

import static pl.com.schoolsystem.security.user.ApplicationRole.STUDENT;
import static pl.com.schoolsystem.security.user.ApplicationUserMapper.APPLICATION_USER_MAPPER;
import static pl.com.schoolsystem.security.user.PasswordGenerator.generatePassword;
import static pl.com.schoolsystem.student.StudentMapper.STUDENT_MAPPER;

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
public class StudentService {

  private final StudentRepository studentRepository;

  private final PasswordService passwordService;

  private final EmailSender emailSender;

  private final ApplicationUserService applicationUserService;

  @Transactional
  public StudentView create(StudentCommand studentCommand) {
    final var password = generatePassword();
    final var applicationUserCommand =
        APPLICATION_USER_MAPPER.toApplicationUserCommand(
            studentCommand, passwordService.encodePassword(password), STUDENT);
    final var applicationUserEntity = applicationUserService.create(applicationUserCommand);
    final var studentEntity = STUDENT_MAPPER.toStudentEntity(applicationUserEntity);
    final var savedEntity = studentRepository.save(studentEntity);
    final var studentId = savedEntity.getId();
    log.info(
        "Created new student with email: {} and id: {}",
        applicationUserEntity.getEmail(),
        studentId);
    emailSender.sendNewUserEmail(applicationUserEntity, password);
    return STUDENT_MAPPER.toStudentView(studentId, applicationUserEntity);
  }

  public StudentView getById(long id) {
    return studentRepository
        .findById(id)
        .map(StudentEntity::getApplicationUser)
        .map(user -> STUDENT_MAPPER.toStudentView(id, user))
        .orElseThrow(() -> new StudentNotFoundException(id));
  }
}
