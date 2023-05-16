package pl.com.schoolsystem.common.exception;

import lombok.Getter;

public class NotFoundException extends RuntimeException {

  @Getter private final String code;
  @Getter private final String message;

  public NotFoundException(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
