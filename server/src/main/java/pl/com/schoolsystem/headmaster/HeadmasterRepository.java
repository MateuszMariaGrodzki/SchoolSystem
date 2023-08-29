package pl.com.schoolsystem.headmaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface HeadmasterRepository
    extends JpaRepository<HeadmasterEntity, Long>, JpaSpecificationExecutor<HeadmasterEntity> {}
