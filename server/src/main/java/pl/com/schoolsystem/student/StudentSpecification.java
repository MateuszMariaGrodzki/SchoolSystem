package pl.com.schoolsystem.student;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import pl.com.schoolsystem.security.user.ApplicationUserEntity_;

@UtilityClass
public class StudentSpecification {

  public Specification<StudentEntity> withId(long id) {
    return (root, query, cb) -> cb.equal(root.get(StudentEntity_.id), id);
  }

  public Specification<StudentEntity> isAccountActive() {
    return (root, query, cb) -> {
      final var applicationUser = root.get(StudentEntity_.applicationUser);
      return cb.equal(applicationUser.get(ApplicationUserEntity_.isExpired), false);
    };
  }
}
