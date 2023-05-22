package pl.com.schoolsystem.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.ADMIN;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
import pl.com.schoolsystem.security.user.ApplicationUserService;

public class AdministratorServiceTest {

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final AdministratorRepository administratorRepository =
      mock(AdministratorRepository.class);

  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

  private final EmailSender emailSender = mock(EmailSender.class);

  private final AdministratorService administratorService =
      new AdministratorService(
          applicationUserService, administratorRepository, passwordEncoder, emailSender);

  @Test
  void shouldRegisterNewAdministrator() {
    // given
    final var command =
        new AddAdministratorCommand("Admin", "Adminowski", "456987123", "admin@admin.pl");
    final var encodedPassword = "jkdsjflsdjflsdjfskldjfsldfjsldfj";
    final var applicationUserEntity = provideApplicationUserEntity(command, encodedPassword);
    final var administratorEntity = provideAdministratorEntity();

    given(passwordEncoder.encode(command.phoneNumber())).willReturn(encodedPassword);
    given(applicationUserService.create(any())).willReturn(applicationUserEntity);
    given(administratorRepository.save(any())).willReturn(administratorEntity);
    doNothing().when(emailSender).sendNewUserEmail(any(), any());
    // when
    final var result = administratorService.create(command);
    // then
    final ArgumentCaptor<AdministratorEntity> administratorCaptor =
        forClass(AdministratorEntity.class);
    verify(administratorRepository).save(administratorCaptor.capture());
    final var savedAdministrator = administratorCaptor.getValue();

    assertResponse(result);
    assertEntity(savedAdministrator.getApplicationUser());
  }

  private void assertEntity(ApplicationUserEntity savedApplicationUser) {
    assertThat(savedApplicationUser.getEmail()).isEqualTo("admin@admin.pl");
    assertThat(savedApplicationUser.getFirstName()).isEqualTo("Admin");
    assertThat(savedApplicationUser.getLastName()).isEqualTo("Adminowski");
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo("456987123");
    assertThat(savedApplicationUser.getRole()).isEqualTo(ADMIN);
  }

  private void assertResponse(AddAdministratorView response) {
    assertThat(response.email()).isEqualTo("admin@admin.pl");
    assertThat(response.firstName()).isEqualTo("Admin");
    assertThat(response.lastName()).isEqualTo("Adminowski");
    assertThat(response.phoneNumber()).isEqualTo("456987123");
  }

  private ApplicationUserEntity provideApplicationUserEntity(
      AddAdministratorCommand command, String password) {
    final var applicationUserEntity = new ApplicationUserEntity();
    applicationUserEntity.setEmail(command.email());
    applicationUserEntity.setPassword(password);
    applicationUserEntity.setRole(ADMIN);
    applicationUserEntity.setPhoneNumber(command.phoneNumber());
    applicationUserEntity.setFirstName(command.firstName());
    applicationUserEntity.setLastName(command.lastName());
    return applicationUserEntity;
  }

  private AdministratorEntity provideAdministratorEntity() {
    final var administratorEntity = new AdministratorEntity();
    administratorEntity.setId(10L);
    return administratorEntity;
  }
}
