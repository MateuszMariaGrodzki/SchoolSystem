package pl.com.schoolsystem.student.api;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
  public StudentView create(@RequestBody @Valid StudentCommand command) {
    return studentService.create(command);
  }
}
