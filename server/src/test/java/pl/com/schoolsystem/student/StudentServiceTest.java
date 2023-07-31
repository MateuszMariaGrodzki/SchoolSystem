package pl.com.schoolsystem.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.STUDENT;
import static pl.com.schoolsystem.student.StudentServiceTestDataFactory.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;

public class StudentServiceTest {

  private final StudentRepository studentRepository = mock(StudentRepository.class);

  private final PasswordService passwordService = mock(PasswordService.class);

  private final EmailSender emailSender = mock(EmailSender.class);

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final StudentService studentService =
      new StudentService(studentRepository, passwordService, emailSender, applicationUserService);

  @Test
  void shouldCreateNewStudent() {
    // given
    final var command =
        new StudentCommand("Student", "Studenciacki", "745981236", "student@student.com.pl");
    final var encodedPassword = "gjkfhklgjhdflghfdljjghfdlgjhfdljkghd";
    final var applicationUserEntity = provideApplicationUserEntity(command, encodedPassword);
    final var studentEntity = provideStudentEntity(875L, 7854L);

    given(applicationUserService.create(any())).willReturn(applicationUserEntity);
    given(studentRepository.save(any())).willReturn(studentEntity);
    doNothing().when(emailSender).sendNewUserEmail(any(), any());
    // when
    final var result = studentService.create(command);
    // then
    final ArgumentCaptor<StudentEntity> studentCaptor = forClass(StudentEntity.class);
    verify(studentRepository).save(studentCaptor.capture());
    final var savedStudent = studentCaptor.getValue();

    assertThat(result.id()).isEqualTo(875L);
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());

    final var savedApplicationUser = savedStudent.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(command.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(command.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(command.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(command.phoneNumber());
    assertThat(savedApplicationUser.getRole()).isEqualTo(STUDENT);
  }
}
