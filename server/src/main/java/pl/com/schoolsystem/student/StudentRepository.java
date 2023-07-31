package pl.com.schoolsystem.student;

import org.springframework.data.jpa.repository.JpaRepository;

interface StudentRepository extends JpaRepository<StudentEntity, Long> {}
