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
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;

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
        new TeacherCommand("Teacher", "Learner", "789456123", "teacher@learner.com");
    final var encodedPassword = "gjfhdgjfhdjgkhdfgkfjdhgkfdhgfdkghd";
    final var applicationUserEntity = provideApplicationUserEntity(command, encodedPassword);
    final var teacherEntity = provideTeacherEntity(132L, 158L);

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
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());

    final var savedApplicationUser = savedTeacher.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(command.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(command.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(command.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(command.phoneNumber());
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
}
