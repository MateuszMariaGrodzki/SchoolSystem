package pl.com.schoolsystem.school;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SchoolCommand(
    @NotBlank(message = "School name is mandatory") String name,
    @NotNull(message = "School tier is mandatory") SchoolLevel tier,
    @Valid @NotNull(message = "Address data is mandatory") AddressCommand address) {}
