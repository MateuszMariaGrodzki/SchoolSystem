package pl.com.schoolsystem.school;

import static pl.com.schoolsystem.school.SchoolLevel.PRIMARY;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SchoolServiceDataTestFactory {

  public SchoolEntity provideSchoolEntity(long id) {
    final var entity = new SchoolEntity();
    entity.setId(id);
    entity.setName("Liceum");
    entity.setTier(PRIMARY);
    entity.setAddress(provideAddressEmbeddableForPostMethod());
    return entity;
  }

  public AddressEmbeddable provideAddressEmbeddableForPostMethod() {
    final var address = new AddressEmbeddable();
    address.setCity("Lublin");
    address.setPostCode("80-666");
    address.setBuilding("70");
    address.setStreet("lubelskiego lipca");
    return address;
  }
}
