package pl.com.schoolsystem.admin;

public record AddAdministratorView(
    Long id, String firstName, String lastName, String email, String phoneNumber) {}
