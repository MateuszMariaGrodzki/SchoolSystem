package pl.com.schoolsystem.school;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressCommand(
    @NotBlank(message = "City is mandatory") String city,
    @NotBlank(message = "Street is mandatory") String street,
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Post code has bad pattern") String postCode,
    @NotBlank(message = "Building is mandatory") String building) {}
