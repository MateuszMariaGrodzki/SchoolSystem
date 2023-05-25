package pl.com.schoolsystem.security.user;

import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidator {

  private static final String DOESNT_MATCH = "doesn't match";

  private static final String NEW_PASSWORD_AND_RETYPED_PASSWORD =
      "new password and retyped password";

  private static final String IS_INCORRECT = "is incorrect";

  private static final String OLD_PASSWORD = "old password";

  private final PasswordEncoder passwordEncoder;

  public Either<Map<String, String>, String> validatePassword(
      ChangePasswordCommand command, ApplicationUserEntity user) {
    final var errors = new HashMap<String, String>();
    return Either.<Map<String, String>, ChangePasswordCommand>right(command)
        .flatMap(createCommand -> validateRetypedPassword(createCommand, errors))
        .flatMap(createCommand -> validateOldPassword(createCommand, user.getPassword(), errors))
        .flatMap(
            createCommand ->
                errors.isEmpty() ? Either.right(createCommand.newPassword()) : Either.left(errors));
  }

  private Either<Map<String, String>, ChangePasswordCommand> validateRetypedPassword(
      ChangePasswordCommand command, Map<String, String> errors) {
    if (validateRetypedPassword(command)) {
      return Either.right(command);
    }
    errors.put(NEW_PASSWORD_AND_RETYPED_PASSWORD, DOESNT_MATCH);
    return Either.right(command);
  }

  private Either<Map<String, String>, ChangePasswordCommand> validateOldPassword(
      ChangePasswordCommand command, String oldPassword, Map<String, String> errors) {
    if (validateOldPassword(command.oldPassword(), oldPassword)) {
      return Either.right(command);
    }
    errors.put(OLD_PASSWORD, IS_INCORRECT);
    return Either.right(command);
  }

  private boolean validateRetypedPassword(ChangePasswordCommand command) {
    return command.newPassword().equals(command.retypedNewPassword());
  }

  private boolean validateOldPassword(String passwordFromRequest, String encryptedPassword) {
    return passwordEncoder.matches(passwordFromRequest, encryptedPassword);
  }
}
