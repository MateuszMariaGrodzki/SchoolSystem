package pl.com.schoolsystem.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface StudentRepository
    extends JpaRepository<StudentEntity, Long>, JpaSpecificationExecutor<StudentEntity> {}
