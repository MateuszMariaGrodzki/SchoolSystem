package pl.com.schoolsystem.security.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {

  Optional<ApplicationUserEntity> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);
}
