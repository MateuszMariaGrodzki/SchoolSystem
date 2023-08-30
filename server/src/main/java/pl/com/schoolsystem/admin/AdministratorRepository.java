package pl.com.schoolsystem.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface AdministratorRepository
    extends JpaRepository<AdministratorEntity, Long>,
        JpaSpecificationExecutor<AdministratorEntity> {}
