package pl.com.schoolsystem.teacher.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.classs.ClasssCommand;
import pl.com.schoolsystem.classs.ClasssService;
import pl.com.schoolsystem.classs.ClasssView;
import pl.com.schoolsystem.teacher.CreateTeacherCommand;
import pl.com.schoolsystem.teacher.TeacherService;
import pl.com.schoolsystem.teacher.TeacherView;
import pl.com.schoolsystem.teacher.UpdateTeacherCommand;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teachers")
@PreAuthorize("hasAnyRole('HEADMASTER')")
public class TeacherController {

  private final TeacherService teacherService;

  private final ClasssService classsService;

  @ResponseStatus(CREATED)
  @PostMapping
  public TeacherView create(@RequestBody @Valid CreateTeacherCommand command) {
    return teacherService.create(command);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('HEADMASTER','TEACHER')")
  public TeacherView getById(@PathVariable long id) {
    return teacherService.getById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('HEADMASTER','TEACHER')")
  public TeacherView updateById(
      @PathVariable long id, @RequestBody @Valid UpdateTeacherCommand command) {
    return teacherService.updateById(id, command);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void deleteById(@PathVariable long id) {
    teacherService.deleteById(id);
  }

  @PostMapping("/{id}/class")
  @ResponseStatus(CREATED)
  @PreAuthorize("hasAnyRole('TEACHER')")
  public ClasssView createClass(@PathVariable long id, @RequestBody @Valid ClasssCommand command) {
    return classsService.create(id, command);
  }
}
