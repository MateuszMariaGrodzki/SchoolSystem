package pl.com.schoolsystem.security.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "application_user")
public class ApplicationUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_user_id_seq")
  @SequenceGenerator(
    name = "application_user_id_seq",
    sequenceName = "application_user_id_seq",
    allocationSize = 1
  )
  private Long id;

  private String firstName;
  private String lastName;
  private String phoneNumber;

  @Column(unique = true)
  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  @Column(updatable = false)
  private ApplicationRole role;
}
