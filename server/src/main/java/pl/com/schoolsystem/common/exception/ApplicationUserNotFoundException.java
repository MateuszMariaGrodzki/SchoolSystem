package pl.com.schoolsystem.common.exception;

import static java.lang.String.format;

public class ApplicationUserNotFoundException extends NotFoundException {

  public ApplicationUserNotFoundException(String email) {
    super("USER_NOT_FOUND", format("User with email %s not found", email));
  }

  public ApplicationUserNotFoundException(long id) {
    super("USER_NOT_FOUND", format("User with id %s not found", id));
  }
}
