package pl.com.schoolsystem.admin;

import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;

import lombok.experimental.UtilityClass;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@UtilityClass
public class AdministratorServiceTestDataFactory {

  public ApplicationUserEntity provideApplicationUserEntity(
      AddAdministratorCommand command, String password) {
    final var applicationUserEntity = new ApplicationUserEntity();
    applicationUserEntity.setEmail(command.email());
    applicationUserEntity.setPassword(password);
    applicationUserEntity.setRole(ADMIN);
    applicationUserEntity.setPhoneNumber(command.phoneNumber());
    applicationUserEntity.setFirstName(command.firstName());
    applicationUserEntity.setLastName(command.lastName());
    return applicationUserEntity;
  }

  public AdministratorEntity provideAdministratorEntity(
      long administratorId, long applicationUserId) {
    final var administratorEntity = new AdministratorEntity();
    administratorEntity.setId(administratorId);
    administratorEntity.setApplicationUser(provideExistingApplicationUser(applicationUserId));
    return administratorEntity;
  }

  private ApplicationUserEntity provideExistingApplicationUser(long applicationUserId) {
    final var applicationUser = new ApplicationUserEntity();
    applicationUser.setId(applicationUserId);
    applicationUser.setEmail("example@example.com.pl");
    applicationUser.setRole(ADMIN);
    applicationUser.setPassword("password");
    applicationUser.setFirstName("FirstName");
    applicationUser.setLastName("LastName");
    applicationUser.setPhoneNumber("147896325");
    return applicationUser;
  }
}
