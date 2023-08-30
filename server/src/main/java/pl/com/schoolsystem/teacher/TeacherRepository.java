package pl.com.schoolsystem.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface TeacherRepository
    extends JpaRepository<TeacherEntity, Long>, JpaSpecificationExecutor<TeacherEntity> {}
