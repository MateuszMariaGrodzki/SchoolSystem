package pl.com.schoolsystem.security.user;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "application_user")
public class ApplicationUserEntity implements UserDetails {

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

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Set.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
