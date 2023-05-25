package pl.com.schoolsystem.security.user;

import io.vavr.control.Either;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

  private final PasswordValidator passwordValidator;

  private final PasswordEncoder passwordEncoder;

  public Either<Map<String, String>, String> changePassword(
      ChangePasswordCommand command, ApplicationUserEntity applicationUser) {
    final var eitherValidationErrorsOrRawPassword =
        passwordValidator.validatePassword(command, applicationUser);
    if (eitherValidationErrorsOrRawPassword.isRight()) {
      log.info("Password changed for user with email {}", applicationUser.getEmail());
      return Either.right(passwordEncoder.encode(command.newPassword()));
    }
    return Either.left(eitherValidationErrorsOrRawPassword.getLeft());
  }

  public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }
}
