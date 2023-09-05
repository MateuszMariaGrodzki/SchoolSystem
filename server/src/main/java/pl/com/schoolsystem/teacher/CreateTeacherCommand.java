package pl.com.schoolsystem.teacher;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.com.schoolsystem.security.user.UserCommand;

public record CreateTeacherCommand(
    @Valid @NotNull(message = "user personal data is mandatory") UserCommand personalData,
    @NotNull(message = "teacher specialisation is mandatory")
        TeacherSpecialization specialization) {}
