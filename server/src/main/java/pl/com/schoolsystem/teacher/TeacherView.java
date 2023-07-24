package pl.com.schoolsystem.teacher;

public record TeacherView(
    Long id, String firstName, String lastName, String email, String phoneNumber) {}
