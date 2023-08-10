package pl.com.schoolsystem.security.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;

public class EmailValidatorTest {

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final EmailValidator emailValidator = new EmailValidator(applicationUserService);

  @Test
  void shouldNotMakeAnotherCallToDatabaseWhenUserIsNotChangingEmail() {
    // given
    final var userEmail = "ona@on.pl";
    final var applicationUserEntity = mock(ApplicationUserEntity.class);

    given(applicationUserEntity.getEmail()).willReturn(userEmail);
    // when
    final var result = emailValidator.isEmailUniqueInDatabase(applicationUserEntity, userEmail);
    // then
    assertThat(result).isTrue();
    verifyNoInteractions(applicationUserService);
  }

  @Test
  void shouldReturnFalseWhenExistsAnotherUserInDatabaseWithProviderEmail() {
    // given
    final var userEmail = "on@ona.pl";
    final var applicationUserEntity = mock(ApplicationUserEntity.class);

    given(applicationUserEntity.getEmail()).willReturn("ona@on.pl");
    given(applicationUserService.existsByEmailIgnoreCase(userEmail)).willReturn(true);
    // when
    final var result = emailValidator.isEmailUniqueInDatabase(applicationUserEntity, userEmail);
    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserChangesEmailToNotExistingInDatabase() {
    // given
    final var userEmail = "ona@on.pl";
    final var applicationUserEntity = mock(ApplicationUserEntity.class);

    given(applicationUserEntity.getEmail()).willReturn("on@ona.pl");
    given(applicationUserService.existsByEmailIgnoreCase(userEmail)).willReturn(false);
    // when
    final var result = emailValidator.isEmailUniqueInDatabase(applicationUserEntity, userEmail);
    // then
    assertThat(result).isTrue();
  }
}
