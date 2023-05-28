package pl.com.schoolsystem.admin;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.admin.AdministratorServiceTestDataFactory.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;
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
        new AddAdministratorCommand("Admin", "Adminowski", "456987123", "admin@admin.pl");
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
    assertThat(result.email()).isEqualTo("admin@admin.pl");
    assertThat(result.firstName()).isEqualTo("Admin");
    assertThat(result.lastName()).isEqualTo("Adminowski");
    assertThat(result.phoneNumber()).isEqualTo("456987123");

    final var savedApplicationUser = savedAdministrator.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo("admin@admin.pl");
    assertThat(savedApplicationUser.getFirstName()).isEqualTo("Admin");
    assertThat(savedApplicationUser.getLastName()).isEqualTo("Adminowski");
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo("456987123");
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

    given(administratorRepository.findById(administratorId)).willReturn(Optional.of(administrator));
    // when
    final var response = administratorService.getById(administratorId);
    // then
    assertThat(response.id()).isEqualTo(administratorId);
    assertThat(response.email()).isEqualTo("example@example.com.pl");
    assertThat(response.firstName()).isEqualTo("FirstName");
    assertThat(response.lastName()).isEqualTo("LastName");
    assertThat(response.phoneNumber()).isEqualTo("147896325");
  }
}
