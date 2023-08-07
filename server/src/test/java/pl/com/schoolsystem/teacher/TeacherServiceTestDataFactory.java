package pl.com.schoolsystem.teacher;

import static pl.com.schoolsystem.security.user.ApplicationRole.TEACHER;

import lombok.experimental.UtilityClass;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.UserCommand;

@UtilityClass
public class TeacherServiceTestDataFactory {

  public ApplicationUserEntity provideApplicationUserEntity(UserCommand command, String password) {
    final var applicationUserEntity = new ApplicationUserEntity();
    applicationUserEntity.setEmail(command.email());
    applicationUserEntity.setPassword(password);
    applicationUserEntity.setRole(TEACHER);
    applicationUserEntity.setPhoneNumber(command.phoneNumber());
    applicationUserEntity.setFirstName(command.firstName());
    applicationUserEntity.setLastName(command.lastName());
    return applicationUserEntity;
  }

  public TeacherEntity provideTeacherEntity(long teacherId, long applicationUserId) {
    final var teacherEntity = new TeacherEntity();
    teacherEntity.setId(teacherId);
    teacherEntity.setApplicationUser(provideExistingApplicationUser(applicationUserId));
    return teacherEntity;
  }

  public ApplicationUserEntity provideExistingApplicationUser(long applicationUserId) {
    final var applicationUser = new ApplicationUserEntity();
    applicationUser.setId(applicationUserId);
    applicationUser.setEmail("teacher@klek.com.pl");
    applicationUser.setRole(TEACHER);
    applicationUser.setPassword("password");
    applicationUser.setFirstName("FirstName");
    applicationUser.setLastName("LastName");
    applicationUser.setPhoneNumber("147896325");
    return applicationUser;
  }
}
