package pl.com.schoolsystem.school;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SchoolCommand(
    @NotBlank(message = "school name is mandatory") String name,
    @NotNull(message = "school tier is mandatory") SchoolLevel tier,
    @Valid @NotNull(message = "address data is mandatory") AddressCommand address) {}
