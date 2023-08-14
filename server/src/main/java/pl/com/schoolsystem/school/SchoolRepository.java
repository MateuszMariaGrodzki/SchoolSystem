package pl.com.schoolsystem.school;

import org.springframework.data.jpa.repository.JpaRepository;

interface SchoolRepository extends JpaRepository<SchoolEntity, Long> {}
