package pl.com.schoolsystem.admin.api;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.admin.AddAdministratorCommand;
import pl.com.schoolsystem.admin.AddAdministratorView;
import pl.com.schoolsystem.admin.AdministratorService;

@RestController
@RequestMapping("/v1/administrators")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdministratorController {

  private final AdministratorService administratorService;

  @ResponseStatus(CREATED)
  @PostMapping
  public AddAdministratorView create(@RequestBody @Valid AddAdministratorCommand command) {
    return administratorService.create(command);
  }
}
