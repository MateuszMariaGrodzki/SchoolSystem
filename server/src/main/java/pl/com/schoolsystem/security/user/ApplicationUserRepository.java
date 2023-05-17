package pl.com.schoolsystem.security.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {

  Optional<ApplicationUserEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
