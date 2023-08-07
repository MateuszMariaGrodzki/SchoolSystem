package pl.com.schoolsystem.teacher;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.TEACHER;
import static pl.com.schoolsystem.teacher.TeacherServiceTestDataFactory.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;
import pl.com.schoolsystem.security.user.UserCommand;

public class TeacherServiceTest {

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final TeacherRepository teacherRepository = mock(TeacherRepository.class);

  private final PasswordService passwordService = mock(PasswordService.class);

  private final EmailSender emailSender = mock(EmailSender.class);

  private final TeacherService teacherService =
      new TeacherService(teacherRepository, passwordService, emailSender, applicationUserService);

  @Test
  void shouldRegisterNewTeacher() {
    // given
    final var command =
        new TeacherCommand(
            new UserCommand("Teacher", "Learner", "789456123", "teacher@learner.com"));
    final var encodedPassword = "gjfhdgjfhdjgkhdfgkfjdhgkfdhgfdkghd";
    final var applicationUserEntity =
        provideApplicationUserEntity(command.personalData(), encodedPassword);
    final var teacherEntity = provideTeacherEntity(132L, 158L);
    final var personalData = command.personalData();

    given(applicationUserService.create(any())).willReturn(applicationUserEntity);
    given(teacherRepository.save(any())).willReturn(teacherEntity);
    doNothing().when(emailSender).sendNewUserEmail(any(), any());
    // when
    final var result = teacherService.create(command);
    // then
    final ArgumentCaptor<TeacherEntity> teacherCaptor = forClass(TeacherEntity.class);
    verify(teacherRepository).save(teacherCaptor.capture());
    final var savedTeacher = teacherCaptor.getValue();

    assertThat(result.id()).isEqualTo(132L);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());

    final var savedApplicationUser = savedTeacher.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(personalData.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(personalData.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(personalData.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(savedApplicationUser.getRole()).isEqualTo(TEACHER);
  }

  @ParameterizedTest
  @ValueSource(longs = {321L, 5430L})
  void shouldFindTeacherInGetMethod(long teacherId) {
    // given
    final var teacher = provideTeacherEntity(teacherId, 456L);

    given(teacherRepository.findById(teacherId)).willReturn(of(teacher));
    // when
    final var result = teacherService.getById(teacherId);
    // then
    assertThat(result.email()).isEqualTo("teacher@klek.com.pl");
    assertThat(result.firstName()).isEqualTo("FirstName");
    assertThat(result.lastName()).isEqualTo("LastName");
    assertThat(result.id()).isEqualTo(teacherId);
    assertThat(result.phoneNumber()).isEqualTo("147896325");
  }

  @Test
  void shouldThrowTeacherNotFoundExceptionInGetByIdMethod() {
    // given
    final var teacherId = 213L;

    given(teacherRepository.findById(teacherId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(TeacherNotFoundException.class, () -> teacherService.getById(teacherId));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Teacher with id 213 not found");
  }

  @ParameterizedTest
  @ValueSource(longs = {658L, 432L})
  void shouldUpdateTeacher(long teacherId) {
    // given
    final var command =
        new TeacherCommand(
            new UserCommand(
                "UpdatedTeac", "Updatedher", "541236987", "updatedteacher@updated.com.pl"));
    final var teacher = provideTeacherEntity(teacherId, 845L);
    final var personalData = command.personalData();

    given(teacherRepository.findById(teacherId)).willReturn(of(teacher));
    given(applicationUserService.existsByEmail(command.personalData().email())).willReturn(false);
    // when
    final var result = teacherService.updateById(teacherId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(result.id()).isEqualTo(teacherId);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
  }

  @Test
  void shouldThrowTeacherNotFoundException() {
    // given
    final var teacherId = 754L;
    final var command =
        new TeacherCommand(
            new UserCommand("Do not", "matter", "541236987", "updatedteacher@updated.com.pl"));

    given(teacherRepository.findById(teacherId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            TeacherNotFoundException.class, () -> teacherService.updateById(teacherId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Teacher with id 754 not found");
    verifyNoInteractions(applicationUserService);
  }

  @Test
  void shouldNotMakeAnotherCallToDatabaseWhenEmailIsTheSameInUpdateMethod() {
    // given
    final var teacherId = 745L;
    final var command =
        new TeacherCommand(
            new UserCommand("Updatable", "teacher", "741236985", "teacher@klek.com.pl"));
    final var teacher = provideTeacherEntity(teacherId, 854L);
    final var personalData = command.personalData();

    given(teacherRepository.findById(teacherId)).willReturn(of(teacher));
    // when
    final var result = teacherService.updateById(teacherId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(result.id()).isEqualTo(teacherId);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    verifyNoInteractions(applicationUserService);
  }

  @Test
  void shouldThrowDuplicatedEmailExceptionInUpdateMethod() {
    // given
    final var teacherId = 45L;
    final var command =
        new TeacherCommand(
            new UserCommand("Updatable", "Teacher", "785412963", "already@existing.com.pl"));
    final var teacher = provideTeacherEntity(teacherId, 3213L);

    given(teacherRepository.findById(teacherId)).willReturn(of(teacher));
    given(applicationUserService.existsByEmail(command.personalData().email())).willReturn(true);
    // when
    final var exception =
        assertThrows(
            DuplicatedApplicationUserEmailException.class,
            () -> teacherService.updateById(teacherId, command));

    // then
    assertThat(exception.getCode()).isEqualTo("DUPLICATED_EMAIL");
    assertThat(exception.getDisplayMessage())
        .isEqualTo("Email: already@existing.com.pl already exists in system");
  }

  @ParameterizedTest
  @ValueSource(longs = {543L, 493242L})
  void shouldDeleteTeacher(long teacherId) {
    // given
    final var applicationUserEntity = mock(ApplicationUserEntity.class);
    final var teacher = new TeacherEntity();
    teacher.setId(teacherId);
    teacher.setApplicationUser(applicationUserEntity);

    given(teacherRepository.findById(teacherId)).willReturn(of(teacher));
    // when
    teacherService.deleteById(teacherId);
    // then
    verify(applicationUserEntity, times(1)).setExpired(true);
  }
}
