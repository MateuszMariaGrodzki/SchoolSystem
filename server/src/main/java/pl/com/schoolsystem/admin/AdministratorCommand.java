package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.common.ApplicationUserValidationConstants.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.com.schoolsystem.security.user.UserCommand;

public record AdministratorCommand(
    @Valid @NotNull(message = "user personal data is mandatory") UserCommand personalData) {}
