package pl.com.schoolsystem.admin;

import org.springframework.data.jpa.repository.JpaRepository;

interface AdministratorRepository extends JpaRepository<AdministratorEntity, Long> {}
