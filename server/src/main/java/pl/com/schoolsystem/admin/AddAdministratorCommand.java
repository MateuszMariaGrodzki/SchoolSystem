package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.common.ApplicationUserValidationConstants.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddAdministratorCommand(
    @NotBlank(message = FIRST_NAME_MANDATORY_MESSAGE)
        @Pattern(regexp = NAME_REGEX, message = NAME_MESSAGE)
        String firstName,
    @NotBlank(message = LAST_NAME_MANDATORY_MESSAGE)
        @Pattern(regexp = NAME_REGEX, message = NAME_MESSAGE)
        String lastName,
    @NotBlank(message = PHONE_NUMBER_MANDATORY_MESSAGE)
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = PHONE_NUMBER_MESSAGE)
        String phoneNumber,
    @NotBlank(message = EMAIL_MANDATORY_MESSAGE)
        @Pattern(regexp = EMAIL_REGEX, message = EMAIL_MESSAGE)
        String email) {}
