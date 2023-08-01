package pl.com.schoolsystem.student;

import static java.lang.String.format;

import pl.com.schoolsystem.common.exception.NotFoundException;

public class StudentNotFoundException extends NotFoundException {

  public StudentNotFoundException(long id) {
    super("USER_NOT_FOUND", format("Teacher with id %s not found", id));
  }
}
