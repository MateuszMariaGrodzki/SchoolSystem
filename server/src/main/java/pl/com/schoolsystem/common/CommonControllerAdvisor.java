package pl.com.schoolsystem.common;

import static java.util.Collections.*;
import static org.springframework.http.HttpStatus.*;
import static pl.com.schoolsystem.common.CommonError.*;
import static pl.com.schoolsystem.common.CommonError.INTERNAL_SERVER_ERROR;

import java.time.Instant;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.com.schoolsystem.common.exception.NotFoundException;

@Slf4j
@ResponseBody
@ControllerAdvice
public class CommonControllerAdvisor {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    log.warn("Method argument not valid: {}", exception.getMessage());

    return new ErrorResponse(
        Instant.now(),
        INVALID_REQUEST.getCode(),
        INVALID_REQUEST.getMessage(),
        exception.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField, FieldError::getDefaultMessage, resolveDuplicatedKey())));
  }

  private BinaryOperator<String> resolveDuplicatedKey() {
    return (k1, k2) -> k1;
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ErrorResponse handleInternalAuthenticationServiceException(
      InternalAuthenticationServiceException exception) {
    log.warn("Authorization failed: {}", exception.getMessage());
    return new ErrorResponse(
        Instant.now(),
        FAILED_AUTHORIZATION.getCode(),
        FAILED_AUTHORIZATION.getMessage(),
        emptyMap());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ErrorResponse handleNotFoundException(NotFoundException exception) {
    log.warn("Entity not found exception occurred: {}", exception.getMessage());
    return new ErrorResponse(
        Instant.now(), exception.getCode(), exception.getMessage(), emptyMap());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse internalServerError(Exception exception) {
    log.error("Internal Server Error: {}", exception.getMessage(), exception);
    return new ErrorResponse(
        Instant.now(),
        INTERNAL_SERVER_ERROR.getCode(),
        INTERNAL_SERVER_ERROR.getMessage(),
        emptyMap());
  }
}
