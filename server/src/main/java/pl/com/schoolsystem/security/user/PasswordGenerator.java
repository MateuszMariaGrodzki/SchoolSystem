package pl.com.schoolsystem.security.user;

import java.util.UUID;

public class PasswordGenerator {

  private static final int PASSWORD_LENGTH = 10;

  public static String generatePassword() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, PASSWORD_LENGTH);
  }
}
