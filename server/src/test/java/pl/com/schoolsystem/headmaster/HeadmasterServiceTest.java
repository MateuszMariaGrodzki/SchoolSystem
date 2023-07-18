package pl.com.schoolsystem.headmaster;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.headmaster.HeadMasterServiceTestDataFactory.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

  @ParameterizedTest
  @ValueSource(longs = {157L, 204L})
  void shouldFindHeadMasterInGetByIdMethod(long headmasterId) {
    // given
    final var applicationUserId = 67L;
    final var headmaster = provideHeadmasterEntity(headmasterId, applicationUserId);

    given(headmasterRepository.findById(headmasterId)).willReturn(of(headmaster));
    // when
    final var result = headmasterService.getById(headmasterId);
    // then
    assertThat(result.email()).isEqualTo("head@master.com.pl");
    assertThat(result.firstName()).isEqualTo("FirstName");
    assertThat(result.lastName()).isEqualTo("LastName");
    assertThat(result.id()).isEqualTo(headmasterId);
    assertThat(result.phoneNumber()).isEqualTo("88148814");
  }

  @Test
  void shouldThrowHeadmasterNotFoundException() {
    // given
    final var headmasterId = 10L;

    given(headmasterRepository.findById(headmasterId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            HeadmasterNotFoundException.class, () -> headmasterService.getById(headmasterId));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Headmaster with id 10 not found");
  }
}
