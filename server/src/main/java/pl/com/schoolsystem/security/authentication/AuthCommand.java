package pl.com.schoolsystem.security.authentication;

import jakarta.validation.constraints.NotBlank;

public record AuthCommand(
    @NotBlank(message = "email is mandatory") String email,
    @NotBlank(message = "password is mandatory") String password) {}
