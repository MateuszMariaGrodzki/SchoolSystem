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
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;
import pl.com.schoolsystem.security.user.UserCommand;

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
        new AdministratorCommand(
            new UserCommand("Admin", "Adminowski", "456987123", "admin@admin.pl"));
    final var randomPassword = "abcderfadsad";
    final var encodedPassword = "jkdsjflsdjflsdjfskldjfsldfjsldfj";
    final var applicationUserEntity =
        provideApplicationUserEntity(command.personalData(), encodedPassword);
    final var administratorEntity = provideAdministratorEntity(123L, 245L);
    final var personalData = command.personalData();

    given(passwordService.generateNewRandomPassword()).willReturn(randomPassword);
    given(passwordService.encodePassword(randomPassword)).willReturn(encodedPassword);
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
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());

    final var savedApplicationUser = savedAdministrator.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(personalData.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(personalData.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(personalData.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(personalData.phoneNumber());
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
            AdministratorNotFoundException.class,
            () -> administratorService.getById(administratorId));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Administrator with id 100054 not found");
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
            new UserCommand(
                "UpdatedFirstName", "UpdatedLastName", "789635412", "updated.email@email.com"));
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);
    final var personalData = command.personalData();

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    given(applicationUserService.existsByEmailIgnoreCase(command.personalData().email()))
        .willReturn(false);
    // when
    final var result = administratorService.updateById(administratorId, command);
    // then
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.id()).isEqualTo(administratorId);
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
  }

  @Test
  public void shouldThrowApplicationUserNotFoundExceptionInUpdateMethod() {
    // given
    final var administratorId = 10L;
    final var command =
        new AdministratorCommand(
            new UserCommand("FirstName", "LastName", "454545454", "email@onet.pl"));

    given(administratorRepository.findById(administratorId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            AdministratorNotFoundException.class,
            () -> administratorService.updateById(administratorId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Administrator with id 10 not found");
  }

  @Test
  public void shouldNotMakeAnotherCallToDatabaseWhenEmailIsTheSame() {
    // given
    final var administratorId = 325L;
    final var applicationUserId = 765L;
    final var command =
        new AdministratorCommand(
            new UserCommand("FirstName", "LastName", "454545454", "example@example.com.pl"));
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);
    final var personalData = command.personalData();

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    // when
    final var result = administratorService.updateById(administratorId, command);
    // then
    verify(applicationUserService, times(0)).existsByEmailIgnoreCase(personalData.email());
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.id()).isEqualTo(administratorId);
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
  }

  @Test
  public void shouldThrowDuplicatedApplicationUserEmailExceptionInUpdateMethod() {
    // given
    final var administratorId = 14L;
    final var applicationUserId = 765L;
    final var command =
        new AdministratorCommand(
            new UserCommand("FirstName", "LastName", "478512369", "already.in@database.com"));
    final var administrator = provideAdministratorEntity(administratorId, applicationUserId);

    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    given(applicationUserService.existsByEmailIgnoreCase(command.personalData().email()))
        .willReturn(true);
    // when
    final var exception =
        assertThrows(
            DuplicatedApplicationUserEmailException.class,
            () -> administratorService.updateById(administratorId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("DUPLICATED_EMAIL");
    assertThat(exception.getDisplayMessage())
        .isEqualTo(format("Email: %s already exists in system", command.personalData().email()));
  }

  @ParameterizedTest
  @ValueSource(longs = {245, 864})
  public void shouldDeleteAdministrator(long administratorId) {
    // given
    final var applicationUserEntity = mock(ApplicationUserEntity.class);
    final var administrator = new AdministratorEntity();
    administrator.setApplicationUser(applicationUserEntity);
    administrator.setId(administratorId);
    given(administratorRepository.findById(administratorId)).willReturn(of(administrator));
    // when
    administratorService.deleteById(administratorId);
    // then
    verify(applicationUserEntity, times(1)).setExpired(true);
  }
}
