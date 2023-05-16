package pl.com.schoolsystem.common.exception;

import org.springframework.security.core.AuthenticationException;

public class SchoolSystemAuthenticationException extends AuthenticationException {

  public SchoolSystemAuthenticationException(String message, Throwable t) {
    super(message, t);
  }
}
