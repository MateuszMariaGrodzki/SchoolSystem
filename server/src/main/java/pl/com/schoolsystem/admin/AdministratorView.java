package pl.com.schoolsystem.admin;

public record AdministratorView(
    Long id, String firstName, String lastName, String email, String phoneNumber) {}
