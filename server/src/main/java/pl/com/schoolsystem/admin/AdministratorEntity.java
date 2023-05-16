package pl.com.schoolsystem.admin;

import static lombok.AccessLevel.PACKAGE;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Entity
@Table(name = "administrator")
@Getter
@Setter(PACKAGE)
public class AdministratorEntity {

  @Id
  @SequenceGenerator(
      name = "administrator_id_seq",
      sequenceName = "administrator_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "administrator_id_seq")
  private Long id;

  @OneToOne
  @JoinColumn(name = "application_user_id")
  private ApplicationUserEntity applicationUser;

  void assignApplicationUser(ApplicationUserEntity applicationUser) {
    this.applicationUser = applicationUser;
  }
}
