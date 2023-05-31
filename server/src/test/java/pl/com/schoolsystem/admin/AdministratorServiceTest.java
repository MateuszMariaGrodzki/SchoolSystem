package pl.com.schoolsystem.admin;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.admin.AdministratorServiceTestDataFactory.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;

public class AdministratorServiceTest {

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final AdministratorRepository administratorRepository =
      mock(AdministratorRepository.class);

  private final PasswordService passwordService = mock(PasswordService.class);

  private final EmailSender emailSender = mock(EmailSender.class);

  private final AdministratorService administratorService =
      new AdministratorService(
          applicationUserService, administratorRepository, passwordService, emailSender);

  @Test
  void shouldRegisterNewAdministrator() {
    // given
    final var command =
        new AdministratorCommand("Admin", "Adminowski", "456987123", "admin@admin.pl");
    final var encodedPassword = "jkdsjflsdjflsdjfskldjfsldfjsldfj";
    final var applicationUserEntity = provideApplicationUserEntity(command, encodedPassword);
    final var administratorEntity = provideAdministratorEntity(123L, 245L);

    given(passwordService.encodePassword(command.phoneNumber())).willReturn(encodedPassword);
    given(applicationUserService.create(any())).willReturn(applicationUserEntity);
    given(administratorRepository.save(any())).willReturn(administratorEntity);
    doNothing().when(emailSender).sendNewUserEmail(any(), any());
    // when
    final var result = administratorService.create(command);
    // then
    final ArgumentCaptor<AdministratorEntity> administratorCaptor =
        forClass(AdministratorEntity.class);
    verify(administratorRepository).save(administratorCaptor.capture());
    final var savedAdministrator = administratorCaptor.getValue();

    assertThat(result.id()).isEqualTo(123L);
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());

    final var savedApplicationUser = savedAdministrator.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(command.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(command.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(command.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(command.phoneNumber());
    assertThat(savedApplicationUser.getRole()).isEqualTo(ADMIN);
  }

  @Test
  public void shouldThrowApplicationUserNotFoundExceptionInGetByIdMethod() {
    // given
    final var administratorId = 100054L;

    given(administratorRepository.findById(administratorId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            ApplicationUserNotFoundException.class,
            () -> administratorService.getById(administratorId));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("User with id 100054 not found");
  }

  @ParameterizedTest
  @ValueSource(longs = {124L, 385L})
  public void shouldFindAdministratorInGetByIdMethod(long administratorId) {
    // given
    final var applicationUserId = 1265L;
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    // when
    final var result = administratorService.getById(administratorId);
    // then
    assertThat(result.id()).isEqualTo(administratorId);
    assertThat(result.email()).isEqualTo("example@example.com.pl");
    assertThat(result.firstName()).isEqualTo("FirstName");
    assertThat(result.lastName()).isEqualTo("LastName");
    assertThat(result.phoneNumber()).isEqualTo("147896325");
  }

  @ParameterizedTest
  @ValueSource(longs = {548L, 5012L})
  public void shouldUpdateAdministrator(long administratorId) {
    // given
    final var applicationUserId = 24L;
    final var command =
        new AdministratorCommand(
            "UpdatedFirstName", "UpdatedLastName", "789635412", "updated.email@email.com");
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    given(applicationUserService.existsByEmail(command.email())).willReturn(false);
    // when
    final var result = administratorService.updateById(administratorId, command);
    // then
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.id()).isEqualTo(administratorId);
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());
  }

  @Test
  public void shouldThrowApplicationUserNotFoundExceptionInUpdateMethod() {
    // given
    final var administratorId = 10L;
    final var command =
        new AdministratorCommand("FirstName", "LastName", "454545454", "email@onet.pl");

    given(administratorRepository.findById(administratorId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            ApplicationUserNotFoundException.class,
            () -> administratorService.updateById(administratorId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("User with id 10 not found");
  }

  @Test
  public void shouldNotMakeAnotherCallToDatabaseWhenEmailIsTheSame() {
    // given
    final var administratorId = 325L;
    final var applicationUserId = 765L;
    final var command =
        new AdministratorCommand("FirstName", "LastName", "454545454", "example@example.com.pl");
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    // when
    final var result = administratorService.updateById(administratorId, command);
    // then
    verify(applicationUserService, times(0)).existsByEmail(command.email());
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.id()).isEqualTo(administratorId);
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());
  }

  @Test
  public void shouldThrowDuplicatedApplicationUserEmailExceptionInUpdateMethod() {
    // given
    final var administratorId = 14L;
    final var applicationUserId = 765L;
    final var command =
        new AdministratorCommand("FirstName", "LastName", "478512369", "already.in@database.com");
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    given(applicationUserService.existsByEmail(command.email())).willReturn(true);
    // when
    final var exception =
        assertThrows(
            DuplicatedApplicationUserEmailException.class,
            () -> administratorService.updateById(administratorId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("DUPLICATED_EMAIL");
    assertThat(exception.getDisplayMessage())
        .isEqualTo(format("Email: %s already exists in system", command.email()));
  }
}
