package pl.com.schoolsystem.student;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.STUDENT;
import static pl.com.schoolsystem.student.StudentServiceTestDataFactory.*;

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
        new StudentCommand(
            new UserCommand("Student", "Studenciacki", "745981236", "student@student.com.pl"));
    final var encodedPassword = "gjkfhklgjhdflghfdljjghfdlgjhfdljkghd";
    final var applicationUserEntity =
        provideApplicationUserEntity(command.personalData(), encodedPassword);
    final var studentEntity = provideStudentEntity(875L, 7854L);
    final var personalData = command.personalData();

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
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());

    final var savedApplicationUser = savedStudent.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(personalData.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(personalData.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(personalData.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(savedApplicationUser.getRole()).isEqualTo(STUDENT);
  }

  @ParameterizedTest
  @ValueSource(longs = {543L, 96549L})
  void shouldFindStudentInGetMethod(long studentId) {
    // given
    final var student = provideStudentEntity(studentId, 654L);

    given(studentRepository.findById(studentId)).willReturn(of(student));
    // when
    final var result = studentService.getById(studentId);
    // then
    assertThat(result.id()).isEqualTo(studentId);
    assertThat(result.email()).isEqualTo("trzezwy@student.com.pl");
    assertThat(result.firstName()).isEqualTo("FirstName");
    assertThat(result.lastName()).isEqualTo("LastName");
    assertThat(result.phoneNumber()).isEqualTo("789654123");
  }

  @Test
  void shouldThrowStudentNotFoundExceptionInGetMethod() {
    // given
    final var studentId = 543L;

    given(studentRepository.findById(studentId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(StudentNotFoundException.class, () -> studentService.getById(studentId));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Student with id 543 not found");
  }

  @ParameterizedTest
  @ValueSource(longs = {543534L, 43L})
  void shouldUpdateStudent(long studentId) {
    // given
    final var command =
        new StudentCommand(
            new UserCommand("Updated", "Student", "745698132", "updatedstudent@com.pl"));
    final var student = provideStudentEntity(studentId, 845L);
    final var personalData = command.personalData();

    given(studentRepository.findById(studentId)).willReturn(of(student));
    given(applicationUserService.existsByEmail(command.personalData().email())).willReturn(false);
    // when
    final var result = studentService.updateById(studentId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(result.id()).isEqualTo(studentId);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
  }

  @Test
  void shouldThrowStudentNotFoundException() {
    // given
    final var studentId = 74591L;
    final var command =
        new StudentCommand(
            new UserCommand("Do not", "matter", "741296835", "sudent@updated.com.pl"));

    given(studentRepository.findById(studentId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            StudentNotFoundException.class, () -> studentService.updateById(studentId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Student with id 74591 not found");
    verifyNoInteractions(applicationUserService);
  }

  @Test
  void shouldNotMakeAnotherCallToDatabaseWhenEmailIsTheSameInUpdateMethod() {
    // given
    final var studentId = 745L;
    final var command =
        new StudentCommand(
            new UserCommand("Updatable", "student", "502188864", "trzezwy@student.com.pl"));
    final var student = provideStudentEntity(studentId, 854L);
    final var personalData = command.personalData();

    given(studentRepository.findById(studentId)).willReturn(of(student));
    // when
    final var result = studentService.updateById(studentId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(result.id()).isEqualTo(studentId);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    verifyNoInteractions(applicationUserService);
  }

  @Test
  void shouldThrowDuplicatedEmailExceptionInUpdateMethod() {
    // given
    final var studentId = 7865L;
    final var command =
        new StudentCommand(
            new UserCommand("Updatable", "Student", "609325789", "already@existing.com.pl"));
    final var teacher = provideStudentEntity(studentId, 3213L);

    given(studentRepository.findById(studentId)).willReturn(of(teacher));
    given(applicationUserService.existsByEmail(command.personalData().email())).willReturn(true);
    // when
    final var exception =
        assertThrows(
            DuplicatedApplicationUserEmailException.class,
            () -> studentService.updateById(studentId, command));

    // then
    assertThat(exception.getCode()).isEqualTo("DUPLICATED_EMAIL");
    assertThat(exception.getDisplayMessage())
        .isEqualTo("Email: already@existing.com.pl already exists in system");
  }

  @ParameterizedTest
  @ValueSource(longs = {54353L, 943242L})
  void shouldDeleteStudent(long studentId) {
    // given
    final var applicationUser = mock(ApplicationUserEntity.class);
    final var student = new StudentEntity();
    student.setApplicationUser(applicationUser);

    given(studentRepository.findById(studentId)).willReturn(of(student));
    // when
    studentService.deleteById(studentId);
    // then
    verify(applicationUser, times(1)).setExpired(true);
  }
}
