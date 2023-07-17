package pl.com.schoolsystem.headmaster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.headmaster.HeadMasterServiceTestDataFactory.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserService;
import pl.com.schoolsystem.security.user.PasswordService;

public class HeadmasterServiceTest {

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final HeadmasterRepository headmasterRepository = mock(HeadmasterRepository.class);

  private final PasswordService passwordService = mock(PasswordService.class);

  private final EmailSender emailSender = mock(EmailSender.class);

  private final HeadmasterService headmasterService =
      new HeadmasterService(
          applicationUserService, headmasterRepository, passwordService, emailSender);

  @Test
  void shouldRegisterNewHeadmaster() {
    // given
    final var command = new HeadmasterCommand("Head", "Master", "794613285", "head@master.pl");
    final var encodedPassword = "jkdsjflsdjflsdjfskldjfsldfjsldfj";
    final var applicationUserEntity = provideApplicationUserEntity(command, encodedPassword);
    final var headmasterEntity = provideHeadmasterEntity(123L, 245L);

    given(passwordService.encodePassword(command.phoneNumber())).willReturn(encodedPassword);
    given(applicationUserService.create(any())).willReturn(applicationUserEntity);
    given(headmasterRepository.save(any())).willReturn(headmasterEntity);
    doNothing().when(emailSender).sendNewUserEmail(any(), any());
    // when
    final var result = headmasterService.create(command);
    // then
    final ArgumentCaptor<HeadmasterEntity> headmasterCaptor = forClass(HeadmasterEntity.class);
    verify(headmasterRepository).save(headmasterCaptor.capture());
    final var savedAdministrator = headmasterCaptor.getValue();

    assertThat(result.id()).isEqualTo(123L);
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());

    final var savedApplicationUser = savedAdministrator.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(command.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(command.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(command.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(command.phoneNumber());
    assertThat(savedApplicationUser.getRole()).isEqualTo(HEADMASTER);
  }
}
