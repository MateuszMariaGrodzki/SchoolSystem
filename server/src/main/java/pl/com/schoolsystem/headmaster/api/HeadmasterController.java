package pl.com.schoolsystem.headmaster.api;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.headmaster.HeadmasterCommand;
import pl.com.schoolsystem.headmaster.HeadmasterService;
import pl.com.schoolsystem.headmaster.HeadmasterView;

@RestController
@PreAuthorize("hasAnyRole('ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/v1/headmasters")
public class HeadmasterController {

  private final HeadmasterService headmasterService;

  @PostMapping
  @ResponseStatus(CREATED)
  public HeadmasterView create(@RequestBody @Valid HeadmasterCommand command) {
    return headmasterService.create(command);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','HEADMASTER')")
  public HeadmasterView getById(@PathVariable long id) {
    return headmasterService.getById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','HEADMASTER')")
  public HeadmasterView updateById(
      @PathVariable long id, @RequestBody @Valid HeadmasterCommand command) {
    return headmasterService.updateById(id, command);
  }
}
