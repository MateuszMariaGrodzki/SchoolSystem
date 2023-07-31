package pl.com.schoolsystem.student;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Entity
@Table(name = "student")
@Getter
@Setter
public class StudentEntity {

  @Id
  @SequenceGenerator(name = "student_id_seq", sequenceName = "student_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_id_seq")
  private Long id;

  @OneToOne
  @JoinColumn(name = "application_user_id")
  private ApplicationUserEntity applicationUser;
}
