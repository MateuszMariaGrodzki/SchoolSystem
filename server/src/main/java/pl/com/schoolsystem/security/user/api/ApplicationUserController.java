package pl.com.schoolsystem.security.user.api;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.common.exception.ValidationException;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.ChangePasswordCommand;

@RestController
@RequestMapping("/v1/passwords")
@PreAuthorize("hasAnyRole('ADMIN', 'HEADMASTER', 'TEACHER', 'STUDENT')")
@RequiredArgsConstructor
public class ApplicationUserController {

  private final ApplicationUserService applicationUserService;

  @PostMapping("/change")
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordCommand command) {
    final var eitherErrorsOrResponse = applicationUserService.changePassword(command);

    return eitherErrorsOrResponse
        .map(a -> ResponseEntity.ok().build())
        .getOrElseGet(
            details ->
                ResponseEntity.status(BAD_REQUEST)
                    .body(
                        new ValidationException(
                            "VALIDATION_EXCEPTION", "password validation failed", details)));
  }
}
