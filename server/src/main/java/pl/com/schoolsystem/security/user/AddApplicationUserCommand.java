package pl.com.schoolsystem.security.user;

public record AddApplicationUserCommand(
    String firstName,
    String lastName,
    String phoneNumber,
    String email,
    String password,
    ApplicationRole role) {}
