package pl.com.schoolsystem.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonError {
  INVALID_REQUEST("VALIDATION_ERROR", "Invalid request");

  private final String code;
  private final String message;
}
