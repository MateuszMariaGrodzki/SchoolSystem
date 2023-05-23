package pl.com.schoolsystem.security.user;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordCommand(
    @NotBlank String oldPassword,
    @NotBlank String newPassword,
    @NotBlank String retypedNewPassword) {}
