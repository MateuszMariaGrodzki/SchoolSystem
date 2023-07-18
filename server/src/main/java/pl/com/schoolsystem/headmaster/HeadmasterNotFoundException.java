package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;

import pl.com.schoolsystem.common.exception.NotFoundException;

public class HeadmasterNotFoundException extends NotFoundException {

  public HeadmasterNotFoundException(long id) {
    super("USER_NOT_FOUND", format("Headmaster with id %s not found", id));
  }
}
