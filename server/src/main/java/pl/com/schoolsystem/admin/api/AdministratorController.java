package pl.com.schoolsystem.admin.api;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.admin.AddAdministratorCommand;
import pl.com.schoolsystem.admin.AdministratorService;
import pl.com.schoolsystem.admin.AdministratorView;

@RestController
@RequestMapping("/v1/administrators")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdministratorController {

  private final AdministratorService administratorService;

  @ResponseStatus(CREATED)
  @PostMapping
  public AdministratorView create(@RequestBody @Valid AddAdministratorCommand command) {
    return administratorService.create(command);
  }

  @GetMapping("/{id}")
  public AdministratorView getById(@PathVariable long id) {
    return administratorService.getById(id);
  }
}
