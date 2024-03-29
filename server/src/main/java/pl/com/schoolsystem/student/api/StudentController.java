package pl.com.schoolsystem.student.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.schoolsystem.student.StudentCommand;
import pl.com.schoolsystem.student.StudentService;
import pl.com.schoolsystem.student.StudentView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/students")
@PreAuthorize("hasAnyRole('TEACHER')")
public class StudentController {

  private final StudentService studentService;

  @ResponseStatus(CREATED)
  @PostMapping
  public StudentView create(@RequestBody @Valid StudentCommand command) {
    return studentService.create(command);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('HEADMASTER','TEACHER','STUDENT')")
  public StudentView getById(@PathVariable long id) {
    return studentService.getById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
  public StudentView updateById(@PathVariable long id, @RequestBody @Valid StudentCommand command) {
    return studentService.updateById(id, command);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void deleteById(@PathVariable long id) {
    studentService.deleteById(id);
  }
}
