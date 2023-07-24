package pl.com.schoolsystem.teacher;

import org.springframework.data.jpa.repository.JpaRepository;

interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {}
