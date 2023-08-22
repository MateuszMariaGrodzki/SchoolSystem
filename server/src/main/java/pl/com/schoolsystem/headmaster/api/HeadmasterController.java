package pl.com.schoolsystem.headmaster.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.headmaster.*;
import pl.com.schoolsystem.school.SchoolCommand;
import pl.com.schoolsystem.school.SchoolView;

@RestController
@PreAuthorize("hasAnyRole('ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/v1/headmasters")
public class HeadmasterController {

  private final HeadmasterService headmasterService;

  @PostMapping
  @ResponseStatus(CREATED)
  public HeadmasterWithSchoolView create(@RequestBody @Valid HeadmasterCommand command) {
    return headmasterService.create(command);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','HEADMASTER')")
  public HeadmasterWithSchoolView getById(@PathVariable long id) {
    return headmasterService.getById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','HEADMASTER')")
  public HeadmasterView updateById(
      @PathVariable long id, @RequestBody @Valid UpdateHeadmasterCommand command) {
    return headmasterService.updateById(id, command);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void deleteById(@PathVariable long id) {
    headmasterService.deleteById(id);
  }

  @PutMapping("/school/{id}")
  @PreAuthorize("hasAnyRole('HEADMASTER')")
  public SchoolView updateSchool(
      @Valid @RequestBody SchoolCommand command, @PathVariable long headmasterId) {
    return headmasterService.updateSchoolByHeadmasterId(headmasterId, command);
  }
}
