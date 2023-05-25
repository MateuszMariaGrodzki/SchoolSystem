package pl.com.schoolsystem.security.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordCommand(
    @NotBlank(message = "old password is mandatory") String oldPassword,
    @NotBlank(message = "new password is mandatory")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,}$",
            message = "Password too weak")
        String newPassword,
    @NotBlank(message = "retyped new password is mandatory") String retypedNewPassword) {}
