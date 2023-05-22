package pl.com.schoolsystem.security.user;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.common.exception.ApplicationUserNotFoundException;

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
}
