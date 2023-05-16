package pl.com.schoolsystem.common.exception;

import static java.util.Collections.emptyMap;

import java.util.Map;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
  private final String displayMessage;
  private final String code;
  private Map<String, String> details;

  public ValidationException(String code, String displayMessage, Map<String, String> details) {
    this.displayMessage = displayMessage;
    this.code = code;
    this.details = details;
  }

  public ValidationException(String code, String displayMessage) {
    this(code, displayMessage, emptyMap());
  }
}
