package pl.com.schoolsystem.admin;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import pl.com.schoolsystem.security.user.ApplicationUserEntity_;

@UtilityClass
public class AdministratorSpecification {

  public Specification<AdministratorEntity> withId(long id) {
    return (root, query, cb) -> cb.equal(root.get(AdministratorEntity_.id), id);
  }

  public Specification<AdministratorEntity> isAccountActive() {
    return (root, query, cb) -> {
      final var applicationUser = root.get(AdministratorEntity_.applicationUser);
      return cb.equal(applicationUser.get(ApplicationUserEntity_.isExpired), false);
    };
  }
}
