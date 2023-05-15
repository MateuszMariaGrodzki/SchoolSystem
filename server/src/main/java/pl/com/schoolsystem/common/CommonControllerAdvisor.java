package pl.com.schoolsystem.common;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pl.com.schoolsystem.common.CommonError.INVALID_REQUEST;

import java.time.Instant;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
}
