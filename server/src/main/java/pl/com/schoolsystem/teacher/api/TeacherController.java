package pl.com.schoolsystem.teacher.api;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.teacher.TeacherCommand;
import pl.com.schoolsystem.teacher.TeacherService;
import pl.com.schoolsystem.teacher.TeacherView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teachers")
@PreAuthorize("hasAnyRole('HEADMASTER')")
public class TeacherController {

  private final TeacherService teacherService;

  @ResponseStatus(CREATED)
  @PostMapping
  public TeacherView create(@RequestBody @Valid TeacherCommand command) {
    return teacherService.create(command);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('HEADMASTER','TEACHER')")
  public TeacherView getById(@PathVariable long id) {
    return teacherService.getById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('HEADMASTER','TEACHER')")
  public TeacherView updateById(@PathVariable long id, @RequestBody @Valid TeacherCommand command) {
    return teacherService.updateById(id, command);
  }
}
