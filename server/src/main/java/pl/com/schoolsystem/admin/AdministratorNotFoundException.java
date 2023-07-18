package pl.com.schoolsystem.admin;

import static java.lang.String.format;

import pl.com.schoolsystem.common.exception.NotFoundException;

public class AdministratorNotFoundException extends NotFoundException {

  public AdministratorNotFoundException(long id) {
    super("USER_NOT_FOUND", format("Administrator with id %s not found", id));
  }
}
