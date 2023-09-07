package pl.com.schoolsystem.teacher;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import pl.com.schoolsystem.security.user.ApplicationUserEntity_;

@UtilityClass
public class TeacherSpecification {

  public Specification<TeacherEntity> withId(long id) {
    return (root, query, cb) -> cb.equal(root.get(TeacherEntity_.id), id);
  }

  public Specification<TeacherEntity> isAccountActive() {
    return (root, query, cb) -> {
      final var applicationUser = root.get(TeacherEntity_.applicationUser);
      return cb.equal(applicationUser.get(ApplicationUserEntity_.isExpired), false);
    };
  }

  public Specification<TeacherEntity> isLoggedUser(long loggedUserId) {
    return (root, query, cb) -> {
      final var applicationUser = root.get(TeacherEntity_.applicationUser);
      return cb.equal(applicationUser.get(ApplicationUserEntity_.id), loggedUserId);
    };
  }
}
