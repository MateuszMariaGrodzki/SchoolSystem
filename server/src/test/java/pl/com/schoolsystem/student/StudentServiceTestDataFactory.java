package pl.com.schoolsystem.student;

import static pl.com.schoolsystem.security.user.ApplicationRole.STUDENT;

import lombok.experimental.UtilityClass;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.UserCommand;

@UtilityClass
public class StudentServiceTestDataFactory {

  public ApplicationUserEntity provideApplicationUserEntity(UserCommand command, String password) {
    final var applicationUserEntity = new ApplicationUserEntity();
    applicationUserEntity.setEmail(command.email());
    applicationUserEntity.setPassword(password);
    applicationUserEntity.setRole(STUDENT);
    applicationUserEntity.setPhoneNumber(command.phoneNumber());
    applicationUserEntity.setFirstName(command.firstName());
    applicationUserEntity.setLastName(command.lastName());
    return applicationUserEntity;
  }

  public StudentEntity provideStudentEntity(long studentId, long applicationUserId) {
    final var studentEntity = new StudentEntity();
    studentEntity.setId(studentId);
    studentEntity.setApplicationUser(provideExistingApplicationUser(applicationUserId));
    return studentEntity;
  }

  public ApplicationUserEntity provideExistingApplicationUser(long applicationUserId) {
    final var applicationUser = new ApplicationUserEntity();
    applicationUser.setId(applicationUserId);
    applicationUser.setEmail("trzezwy@student.com.pl");
    applicationUser.setRole(STUDENT);
    applicationUser.setPassword("password");
    applicationUser.setFirstName("FirstName");
    applicationUser.setLastName("LastName");
    applicationUser.setPhoneNumber("789654123");
    return applicationUser;
  }
}
