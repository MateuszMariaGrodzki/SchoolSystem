package pl.com.schoolsystem.headmaster;

import static pl.com.schoolsystem.school.SchoolLevel.HIGH;
import static pl.com.schoolsystem.school.SchoolLevel.PRIMARY;
import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;

import lombok.experimental.UtilityClass;
import pl.com.schoolsystem.school.AddressEmbeddable;
import pl.com.schoolsystem.school.SchoolEntity;
import pl.com.schoolsystem.school.SchoolView;
import pl.com.schoolsystem.security.user.AddApplicationUserCommand;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.UserCommand;

@UtilityClass
public class HeadmasterServiceTestDataFactory {

  public ApplicationUserEntity provideApplicationUserEntity(UserCommand command, String password) {
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
    headmasterEntity.setSchool(provideSchoolEntity(headmasterId));
    return headmasterEntity;
  }

  private SchoolEntity provideSchoolEntity(long id) {
    final var schoolEntity = new SchoolEntity();
    schoolEntity.setId(id);
    schoolEntity.setName("Liceum kopernika");
    schoolEntity.setTier(PRIMARY);
    schoolEntity.setAddress(provideAddressEmbeddable());
    return schoolEntity;
  }

  private AddressEmbeddable provideAddressEmbeddable() {
    final var addressEmbeddable = new AddressEmbeddable();
    addressEmbeddable.setStreet("Wojciechowska");
    addressEmbeddable.setCity("Warszawa");
    addressEmbeddable.setBuilding("8/74");
    addressEmbeddable.setPostCode("88-666");
    return addressEmbeddable;
  }

  public SchoolView provideSchoolView() {
    return new SchoolView(10L, "Liceum ogólnokształcące", HIGH, "Lublin", "Zana", "88-666", "8/1");
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

  public AddApplicationUserCommand provideApplicationUserCommandForCreateMethod() {
    return new AddApplicationUserCommand(
        "Head",
        "Master",
        "794613285",
        "head@master.pl",
        "jkdsjflsdjflsdjfskldjfsldfjsldfj",
        HEADMASTER);
  }
}
