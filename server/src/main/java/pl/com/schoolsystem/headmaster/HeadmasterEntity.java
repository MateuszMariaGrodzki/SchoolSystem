package pl.com.schoolsystem.headmaster;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Entity
@Table(name = "headmaster")
@Getter
@Setter
public class HeadmasterEntity {

  @Id
  @SequenceGenerator(
      name = "headmaster_id_seq",
      sequenceName = "headmaster_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "headmaster_id_seq")
  private Long id;

  @OneToOne
  @JoinColumn(name = "application_user_id")
  private ApplicationUserEntity applicationUser;
}
