package pl.com.schoolsystem.school;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressCommand(
    @NotBlank(message = "city is mandatory") String city,
    @NotBlank(message = "street is mandatory") String street,
    @Pattern(regexp = "//d{3}-//d{2}", message = "Post code has bad pattern") String postCode,
    @NotBlank(message = "building is mandatory") String building) {}
