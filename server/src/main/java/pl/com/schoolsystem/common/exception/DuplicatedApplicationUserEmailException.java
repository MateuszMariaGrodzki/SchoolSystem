package pl.com.schoolsystem.common.exception;

import static java.lang.String.format;

public class DuplicatedApplicationUserEmailException extends ValidationException {
  public DuplicatedApplicationUserEmailException(String displayMessage) {
    super("DUPLICATED_EMAIL", format("Email: %s exists in system", displayMessage));
  }
}
