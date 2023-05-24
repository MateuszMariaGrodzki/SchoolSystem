package pl.com.schoolsystem.security.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordValidatorTest {

  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

  private final PasswordValidator passwordValidator = new PasswordValidator(passwordEncoder);

  @Test
  void shouldFailValidationWhenNewPasswordAndRetypedPasswordAreDifferent() {
    // given
    final var command = new ChangePasswordCommand("irrelevant", "kotamaAla", "Alamakota");
    final var applicationUser = mock(ApplicationUserEntity.class);

    given(applicationUser.getPassword()).willReturn(command.oldPassword());
    given(passwordEncoder.matches(command.oldPassword(), command.oldPassword())).willReturn(true);
    // when
    final var result = passwordValidator.validatePassword(command, applicationUser);
    // then
    assertThat(result.isLeft()).isTrue();
    final var eitherExtracted = result.getLeft();
    assertThat(eitherExtracted).hasSize(1);
    assertThat(eitherExtracted.get(0)).isEqualTo("password and retyped password doesn't match");
  }

  @Test
  void shouldFailValidationWhenOldPasswordIsWrong() {
    // given
    final var command =
        new ChangePasswordCommand(
            "wrong old password", "strong new password", "strong new password");
    final var applicationUser = mock(ApplicationUserEntity.class);

    given(applicationUser.getPassword()).willReturn("old password");
    given(passwordEncoder.matches(command.oldPassword(), "old password")).willReturn(false);
    // when
    final var result = passwordValidator.validatePassword(command, applicationUser);
    // then
    assertThat(result.isLeft()).isTrue();
    final var eitherExtracted = result.getLeft();
    assertThat(eitherExtracted).hasSize(1);
    assertThat(eitherExtracted.get(0)).isEqualTo("old password is incorrect");
  }

  @Test
  void shouldFailValidationWhenNewPasswordAndRetypedPasswordAreDifferentAndOldPasswordIsWrong() {
    // given
    final var command =
        new ChangePasswordCommand(
            "wrong old password", "strong new password", "password strong new");
    final var applicationUser = mock(ApplicationUserEntity.class);

    given(applicationUser.getPassword()).willReturn("old password");
    given(passwordEncoder.matches(command.oldPassword(), "old password")).willReturn(false);
    // when
    final var result = passwordValidator.validatePassword(command, applicationUser);
    // then
    assertThat(result.isLeft()).isTrue();
    final var eitherExtracted = result.getLeft();
    assertThat(eitherExtracted).hasSize(2);
    assertThat(eitherExtracted.get(0)).isEqualTo("password and retyped password doesn't match");
    assertThat(eitherExtracted.get(1)).isEqualTo("old password is incorrect");
  }

  @Test
  void shouldPassValidation() {
    // given
    final var command =
        new ChangePasswordCommand("old password", "strong password", "strong password");
    final var applicationUser = mock(ApplicationUserEntity.class);

    given(applicationUser.getPassword()).willReturn("old password");
    given(passwordEncoder.matches(command.oldPassword(), "old password")).willReturn(true);
    // when
    final var result = passwordValidator.validatePassword(command, applicationUser);
    // then
    assertThat(result.isRight()).isTrue();
    assertThat(result.get()).isEqualTo("strong password");
  }
}
