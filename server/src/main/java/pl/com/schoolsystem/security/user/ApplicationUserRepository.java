package pl.com.schoolsystem.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {}
