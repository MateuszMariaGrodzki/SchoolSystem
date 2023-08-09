package pl.com.schoolsystem.security.user;

import io.vavr.control.Either;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

  private static final int PASSWORD_LENGTH = 10;

  private final PasswordValidator passwordValidator;

  private final PasswordEncoder passwordEncoder;

  public Either<Map<String, String>, String> changePassword(
      ChangePasswordCommand command, ApplicationUserEntity applicationUser) {
    final var eitherValidationErrorsOrRawPassword =
        passwordValidator.validatePassword(command, applicationUser);

    return eitherValidationErrorsOrRawPassword
        .map(this::encodePassword)
        .orElse(() -> Either.left(eitherValidationErrorsOrRawPassword.getLeft()));
  }

  public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  public String generateNewRandomPassword() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, PASSWORD_LENGTH);
  }
}
