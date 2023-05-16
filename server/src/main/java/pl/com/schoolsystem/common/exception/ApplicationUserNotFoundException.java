package pl.com.schoolsystem.common.exception;

import static java.lang.String.format;

public class ApplicationUserNotFoundException extends NotFoundException {

  public ApplicationUserNotFoundException(String email) {
    super("USER_NOT_FOUND", format("User with email %s not found", email));
  }
}
