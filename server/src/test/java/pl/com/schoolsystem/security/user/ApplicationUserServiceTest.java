package pl.com.schoolsystem.security.user;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;

public class ApplicationUserServiceTest {

  private final ApplicationUserRepository applicationUserRepository =
      mock(ApplicationUserRepository.class);

  private final ApplicationUserService applicationUserService =
      new ApplicationUserService(applicationUserRepository);

  @Test
  void shouldThorApplicationUserNotFoundExceptionOnGetByEmailMethod() {
    // given
    final var email = "qwerty@onet.pl";
    given(applicationUserRepository.findByEmail(email)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            ApplicationUserNotFoundException.class,
            () ->
                applicationUserService.getByEmailsOrElseThrowApplicationUserNotFoundException(
                    email));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("User with email qwerty@onet.pl not found");
  }

  @Test
  void shouldGetApplicationUserByEmail() {
    // given
    final var email = "qwerty@onet.pl";
    given(applicationUserRepository.findByEmail(email))
        .willReturn(Optional.of(new ApplicationUserEntity()));
    // when
    // then
    assertThatCode(
            () ->
                applicationUserService.getByEmailsOrElseThrowApplicationUserNotFoundException(
                    email))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @EnumSource(value = ApplicationRole.class)
  void shouldThrowExceptionWhenEmailAlreadyExistsInDatabase(ApplicationRole userRole) {
    // given
    final var command =
        new AddApplicationUserCommand(
            "Admin",
            "Adminowski",
            "6587412",
            "qwerty@onet.pl",
            "dsadasgjdkjghsfgfdkgbfdkgd",
            userRole);
    given(applicationUserRepository.existsByEmail(command.email())).willReturn(true);
    // when
    final var exception =
        assertThrows(
            DuplicatedApplicationUserEmailException.class,
            () -> applicationUserService.create(command));
    // then
    assertThat(exception.getCode()).isEqualTo("DUPLICATED_EMAIL");
    assertThat(exception.getDisplayMessage())
        .isEqualTo("Email: qwerty@onet.pl already exists in system");
  }

  @ParameterizedTest
  @EnumSource(value = ApplicationRole.class)
  void shouldCreateNewApplicationUser(ApplicationRole userRole) {
    // given
    final var command =
        new AddApplicationUserCommand(
            "User",
            "Userowski",
            "123698745",
            "qwerty@onet.pl",
            "dsadasgjdkjghsfgfdkgbfdkgd",
            userRole);
    final var applicationUser = mock(ApplicationUserEntity.class);
    given(applicationUser.getId()).willReturn(10L);
    given(applicationUser.getEmail()).willReturn("aaa");

    given(applicationUserRepository.save(any())).willReturn(applicationUser);
    given(applicationUserRepository.existsByEmail(command.email())).willReturn(false);
    // when
    applicationUserService.create(command);
    // then
    final ArgumentCaptor<ApplicationUserEntity> applicationUserCaptor =
        forClass(ApplicationUserEntity.class);
    verify(applicationUserRepository).save(applicationUserCaptor.capture());
    assertApplicationUser(applicationUserCaptor.getValue(), userRole);
  }

  private void assertApplicationUser(
      ApplicationUserEntity applicationUserEntity, ApplicationRole role) {
    assertThat(applicationUserEntity.getEmail()).isEqualTo("qwerty@onet.pl");
    assertThat(applicationUserEntity.getRole()).isEqualTo(role);
    assertThat(applicationUserEntity.getPhoneNumber()).isEqualTo("123698745");
    assertThat(applicationUserEntity.getFirstName()).isEqualTo("User");
    assertThat(applicationUserEntity.getLastName()).isEqualTo("Userowski");
  }
}
