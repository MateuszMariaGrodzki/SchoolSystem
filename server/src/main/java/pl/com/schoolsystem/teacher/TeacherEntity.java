package pl.com.schoolsystem.teacher;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Entity
@Table(name = "teacher")
@Getter
@Setter
public class TeacherEntity {

  @Id
  @SequenceGenerator(name = "teacher_id_seq", sequenceName = "teacher_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacher_id_seq")
  private Long id;

  @OneToOne
  @JoinColumn(name = "application_user_id")
  private ApplicationUserEntity applicationUser;
}
