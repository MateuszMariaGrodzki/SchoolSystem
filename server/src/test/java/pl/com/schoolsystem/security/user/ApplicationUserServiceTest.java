package pl.com.schoolsystem.security.user;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;

import java.util.Optional;
import org.junit.jupiter.api.Test;
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

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExistsInDatabase() {
    // given
    final var command =
        new AddApplicationUserCommand(
            "Admin",
            "Adminowski",
            "6587412",
            "qwerty@onet.pl",
            "dsadasgjdkjghsfgfdkgbfdkgd",
            ADMIN);
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
}
