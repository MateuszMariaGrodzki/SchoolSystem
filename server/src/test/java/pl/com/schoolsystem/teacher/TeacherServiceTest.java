package pl.com.schoolsystem.teacher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.TEACHER;
import static pl.com.schoolsystem.teacher.TeacherServiceTestDataFactory.*;

import org.junit.jupiter.api.Test;
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
}
