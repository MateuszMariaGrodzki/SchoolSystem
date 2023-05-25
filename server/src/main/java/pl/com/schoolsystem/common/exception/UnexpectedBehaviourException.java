package pl.com.schoolsystem.common.exception;

import lombok.Getter;

public class UnexpectedBehaviourException extends RuntimeException {
  @Getter private final String code;
  @Getter private final String displayMessage;

  public UnexpectedBehaviourException(String code, String displayMessage) {
    this.code = code;
    this.displayMessage = displayMessage;
  }
}
