package pl.com.schoolsystem.security.user;

import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidator {

  private static final String NEW_PASSWORD_AND_RETYPED_NEW_PASSWORD_DIFFERENT_MESSAGE =
      "password and retyped password doesn't match";

  private static final String WRONG_OLD_PASSWORD_MESSAGE = "old password is incorrect";

  private final PasswordEncoder passwordEncoder;

  public Either<List<String>, String> validatePassword(
      ChangePasswordCommand command, ApplicationUserEntity user) {
    final var errors = new ArrayList<String>();
    return Either.<List<String>, ChangePasswordCommand>right(command)
        .flatMap(createCommand -> validateRetypedPassword(createCommand, errors))
        .flatMap(createCommand -> validateOldPassword(createCommand, user.getPassword(), errors))
        .flatMap(
            createCommand ->
                errors.isEmpty() ? Either.right(createCommand.newPassword()) : Either.left(errors));
  }

  private Either<List<String>, ChangePasswordCommand> validateRetypedPassword(
      ChangePasswordCommand command, List<String> errors) {
    if (validateRetypedPassword(command)) {
      return Either.right(command);
    }
    errors.add(NEW_PASSWORD_AND_RETYPED_NEW_PASSWORD_DIFFERENT_MESSAGE);
    return Either.right(command);
  }

  private Either<List<String>, ChangePasswordCommand> validateOldPassword(
      ChangePasswordCommand command, String oldPassword, List<String> errors) {
    if (validateOldPassword(command.oldPassword(), oldPassword)) {
      return Either.right(command);
    }
    errors.add(WRONG_OLD_PASSWORD_MESSAGE);
    return Either.right(command);
  }

  private boolean validateRetypedPassword(ChangePasswordCommand command) {
    return command.newPassword().equals(command.retypedNewPassword());
  }

  private boolean validateOldPassword(String passwordFromRequest, String encryptedPassword) {
    return passwordEncoder.matches(passwordFromRequest, encryptedPassword);
  }
}
