package pl.com.schoolsystem.teacher;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.com.schoolsystem.security.user.UserCommand;

public record UpdateTeacherCommand(
    @Valid @NotNull(message = "user personal data is mandatory") UserCommand personalData) {}
