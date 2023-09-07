package pl.com.schoolsystem.classs;

public class ClassNotCreatedException extends RuntimeException {

  public ClassNotCreatedException() {
    super("There is no defined class for this user");
  }
}
