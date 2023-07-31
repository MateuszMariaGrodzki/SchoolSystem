package pl.com.schoolsystem.student;

public record StudentView(
    Long id, String firstName, String lastName, String email, String phoneNumber) {}
