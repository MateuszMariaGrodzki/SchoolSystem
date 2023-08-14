package pl.com.schoolsystem.headmaster;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.com.schoolsystem.school.SchoolCommand;
import pl.com.schoolsystem.security.user.UserCommand;

public record HeadmasterCommand(
    @Valid @NotNull(message = "user personal data is mandatory") UserCommand personalData,
    @Valid @NotNull(message = "school data is mandatory") SchoolCommand schoolData) {}
