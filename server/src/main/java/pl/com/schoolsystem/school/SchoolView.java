package pl.com.schoolsystem.school;

public record SchoolView(
    Long id,
    String name,
    SchoolLevel tier,
    String city,
    String street,
    String postCode,
    String building) {}
