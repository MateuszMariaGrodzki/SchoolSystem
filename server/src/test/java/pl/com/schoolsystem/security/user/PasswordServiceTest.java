package pl.com.schoolsystem.security.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import io.vavr.control.Either;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordServiceTest {

  private final PasswordValidator passwordValidator = mock(PasswordValidator.class);

  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

  private final PasswordService passwordService =
      new PasswordService(passwordValidator, passwordEncoder);

  @Test
  void shouldReturnEitherWithValidationErrors() {
    // given
    final var command = new ChangePasswordCommand("AlamaKota1!", "AlamaKota1!", "AlaNieMaKota1!");
    final var applicationUser = mock(ApplicationUserEntity.class);
    final var violationsList = List.of("wrong password", "incorrect old password");

    given(passwordValidator.validatePassword(command, applicationUser))
        .willReturn(Either.left(violationsList));
    // when
    final var result = passwordService.changePassword(command, applicationUser);
    // then
    assertThat(result.isLeft()).isTrue();
    final var extractedEither = result.getLeft();
    assertThat(extractedEither).hasSize(2);
    assertThat(extractedEither.get(0)).isEqualTo("wrong password");
    assertThat(extractedEither.get(1)).isEqualTo("incorrect old password");
    verify(applicationUser, times(0)).setPassword(any());
  }

  @Test
  void shouldEncryptUserPassword() {
    // given
    final var command = new ChangePasswordCommand("AlamaKota1!", "AlamaKota1!", "AlaNieMaKota1!");
    final var applicationUser = mock(ApplicationUserEntity.class);
    final var encryptedPassword = "encryptedAlamaKota1!";

    given(passwordValidator.validatePassword(command, applicationUser))
        .willReturn(Either.right(command.newPassword()));
    given(passwordEncoder.encode(command.newPassword())).willReturn(encryptedPassword);
    // when
    final var result = passwordService.changePassword(command, applicationUser);
    // then
    assertThat(result.isRight()).isTrue();
    final var extractedEither = result.get();
    assertThat(extractedEither).isEqualTo(encryptedPassword);
  }
}
