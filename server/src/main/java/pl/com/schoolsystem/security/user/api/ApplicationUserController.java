package pl.com.schoolsystem.security.user.api;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.ChangePasswordCommand;

@RestController
@RequestMapping("/v1/passwords")
@PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TEACHER', 'STUDENT')")
@RequiredArgsConstructor
public class ApplicationUserController {

  private final ApplicationUserService applicationUserService;

  @PostMapping("/change")
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordCommand command) {
    final var eitherErrorsOrResponse = applicationUserService.changePassword(command);

    if (eitherErrorsOrResponse.isLeft()) {
      return ResponseEntity.status(BAD_REQUEST).body(eitherErrorsOrResponse.getLeft());
    }
    return ResponseEntity.ok().build();
  }
}
