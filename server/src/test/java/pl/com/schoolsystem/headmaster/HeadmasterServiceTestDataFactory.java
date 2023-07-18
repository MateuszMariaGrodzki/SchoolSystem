package pl.com.schoolsystem.headmaster;

import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;

import lombok.experimental.UtilityClass;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@UtilityClass
public class HeadmasterServiceTestDataFactory {

  public ApplicationUserEntity provideApplicationUserEntity(
      HeadmasterCommand command, String password) {
    final var applicationUserEntity = new ApplicationUserEntity();
    applicationUserEntity.setEmail(command.email());
    applicationUserEntity.setPassword(password);
    applicationUserEntity.setRole(HEADMASTER);
    applicationUserEntity.setPhoneNumber(command.phoneNumber());
    applicationUserEntity.setFirstName(command.firstName());
    applicationUserEntity.setLastName(command.lastName());
    return applicationUserEntity;
  }

  public HeadmasterEntity provideHeadmasterEntity(long headmasterId, long applicationUserId) {
    final var headmasterEntity = new HeadmasterEntity();
    headmasterEntity.setId(headmasterId);
    headmasterEntity.setApplicationUser(provideExistingApplicationUser(applicationUserId));
    return headmasterEntity;
  }

  private ApplicationUserEntity provideExistingApplicationUser(long applicationUserId) {
    final var applicationUser = new ApplicationUserEntity();
    applicationUser.setId(applicationUserId);
    applicationUser.setEmail("head@master.com.pl");
    applicationUser.setRole(HEADMASTER);
    applicationUser.setPassword("password");
    applicationUser.setFirstName("FirstName");
    applicationUser.setLastName("LastName");
    applicationUser.setPhoneNumber("88148814");
    return applicationUser;
  }
}
