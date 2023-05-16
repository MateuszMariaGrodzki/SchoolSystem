package pl.com.schoolsystem.admin;

import jakarta.validation.constraints.NotBlank;

public record AddAdministratorCommand(
    @NotBlank(message = "name is mandatory") String firstName,
    @NotBlank(message = "last name is mandatory") String lastName,
    @NotBlank(message = "phone number is mandatory") String phoneNumber,
    @NotBlank(message = "email is mandatory") String email) {}
