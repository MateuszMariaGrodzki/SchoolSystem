package pl.com.schoolsystem.security.user;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class ApplicationUserSpecification {

  public Specification<ApplicationUserEntity> emailEqualsIgnoreCase(String email) {
    return (root, query, cb) ->
        cb.equal(cb.lower(root.get(ApplicationUserEntity_.email)), email.toLowerCase());
  }
}
