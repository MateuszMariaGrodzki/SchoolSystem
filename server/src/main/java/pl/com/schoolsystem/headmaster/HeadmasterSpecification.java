package pl.com.schoolsystem.headmaster;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import pl.com.schoolsystem.security.user.ApplicationUserEntity_;

@UtilityClass
public class HeadmasterSpecification {

  public Specification<HeadmasterEntity> withId(long id) {
    return (root, query, cb) -> cb.equal(root.get(HeadmasterEntity_.id), id);
  }

  public Specification<HeadmasterEntity> isAccountActive() {
    return (root, query, cb) -> {
      final var applicationUser = root.get(HeadmasterEntity_.applicationUser);
      return cb.equal(applicationUser.get(ApplicationUserEntity_.isExpired), false);
    };
  }

  public Specification<HeadmasterEntity> isLoggedUser(long loggedUserId) {
    return (root, query, cb) -> {
      final var applicationUser = root.get(HeadmasterEntity_.applicationUser);
      return cb.equal(applicationUser.get(ApplicationUserEntity_.id), loggedUserId);
    };
  }
}
