package pl.com.schoolsystem.admin.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.admin.AdministratorCommand;
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
  public AdministratorView create(@RequestBody @Valid AdministratorCommand command) {
    return administratorService.create(command);
  }

  @GetMapping("/{id}")
  public AdministratorView getById(@PathVariable long id) {
    return administratorService.getById(id);
  }

  @PutMapping("/{id}")
  public AdministratorView updateById(
      @PathVariable long id, @RequestBody @Valid AdministratorCommand command) {
    return administratorService.updateById(id, command);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void deleteById(@PathVariable long id) {
    administratorService.deleteById(id);
  }
}
