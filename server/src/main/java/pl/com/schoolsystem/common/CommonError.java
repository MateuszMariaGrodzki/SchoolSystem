package pl.com.schoolsystem.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonError {
  INVALID_REQUEST("VALIDATION_ERROR", "Invalid request"),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Something went wrong"),
  FAILED_AUTHORIZATION("UNAUTHORIZED", "Failed authorization");

  private final String code;
  private final String message;
}