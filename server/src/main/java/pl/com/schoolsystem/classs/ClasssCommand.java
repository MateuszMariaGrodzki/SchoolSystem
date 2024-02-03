package pl.com.schoolsystem.classs;

import jakarta.validation.constraints.NotNull;

public record ClasssCommand(@NotNull(message = "profile is mandatory") ClasssProfile profile) {}
