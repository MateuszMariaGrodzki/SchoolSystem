package pl.com.schoolsystem.headmaster;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.com.schoolsystem.security.user.UserCommand;

public record UpdateHeadmasterCommand(
    @Valid @NotNull(message = "user personal data is mandatory") UserCommand personalData) {}
