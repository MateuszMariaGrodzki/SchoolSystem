package pl.com.schoolsystem.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface ApplicationUserRepository
    extends JpaRepository<ApplicationUserEntity, Long>,
        JpaSpecificationExecutor<ApplicationUserEntity> {}
