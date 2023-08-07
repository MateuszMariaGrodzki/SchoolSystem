package pl.com.schoolsystem.teacher;

import static pl.com.schoolsystem.common.ApplicationUserValidationConstants.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.com.schoolsystem.security.user.UserCommand;

public record TeacherCommand(
    @Valid @NotNull(message = "user personal data is mandatory") UserCommand personalData) {}
