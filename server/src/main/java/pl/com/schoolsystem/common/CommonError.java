package pl.com.schoolsystem.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonError {
  INVALID_REQUEST("VALIDATION_ERROR", "Invalid request"),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Something went wrong"),
  FAILED_AUTHORIZATION("UNAUTHORIZED", "Failed authorization"),
  ACCOUNT_EXPIRED("FORBIDDEN", "Account has been deleted"),
  ACCESS_DENIED("ACCESS_DENIED", "Acces is denied");

  private final String code;
  private final String message;
}
