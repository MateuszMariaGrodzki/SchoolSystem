package pl.com.schoolsystem.teacher;

import static java.lang.String.format;

import pl.com.schoolsystem.common.exception.NotFoundException;

public class TeacherNotFoundException extends NotFoundException {
  public TeacherNotFoundException(long id) {
    super("USER_NOT_FOUND", format("Teacher with id %s not found", id));
  }
}
