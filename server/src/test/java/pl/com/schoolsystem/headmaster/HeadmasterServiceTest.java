package pl.com.schoolsystem.headmaster;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.com.schoolsystem.headmaster.HeadmasterServiceTestDataFactory.*;
import static pl.com.schoolsystem.security.user.ApplicationRole.HEADMASTER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.common.exception.DuplicatedApplicationUserEmailException;
import pl.com.schoolsystem.mail.EmailSender;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;
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
  void shouldThrowHeadmasterNotFoundExceptionInGetMethod() {
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

  @ParameterizedTest
  @ValueSource(longs = {985L, 231L})
  void shouldUpdateHeadmaster(long headmasterId) {
    // given
    final var command =
        new HeadmasterCommand("UpdatedHead", "UpdatedMaster", "75315991", "head@master.com.pl");
    final var headmaster = provideHeadmasterEntity(headmasterId, 147L);

    given(headmasterRepository.findById(headmasterId)).willReturn(of(headmaster));
    // when
    final var result = headmasterService.updateById(headmasterId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());
    assertThat(result.id()).isEqualTo(headmasterId);
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
  }

  @Test
  void shouldThrowHeadmasterNotFoundExceptionInUpdateMethod() {
    // given
    final var headmasterId = 134L;
    final var command =
        new HeadmasterCommand("Not", "Existing", "789456123", "not@existing.com.pl");

    given(headmasterRepository.findById(headmasterId)).willReturn(empty());
    // when
    final var exception =
        assertThrows(
            HeadmasterNotFoundException.class,
            () -> headmasterService.updateById(headmasterId, command));
    // then
    assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND");
    assertThat(exception.getMessage()).isEqualTo("Headmaster with id 134 not found");
  }

  @Test
  void shouldNotMakeAnotherCallToDatabaseWhenEmailIsTheSame() {
    // given
    final var headmasterId = 145L;
    final var command = new HeadmasterCommand("Not", "The same", "741236985", "head@master.com.pl");
    final var headmaster = provideHeadmasterEntity(145L, 234L);

    given(headmasterRepository.findById(headmasterId)).willReturn(of(headmaster));
    // when
    final var result = headmasterService.updateById(headmasterId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(command.phoneNumber());
    assertThat(result.id()).isEqualTo(headmasterId);
    assertThat(result.email()).isEqualTo(command.email());
    assertThat(result.firstName()).isEqualTo(command.firstName());
    assertThat(result.lastName()).isEqualTo(command.lastName());
    verifyNoInteractions(applicationUserService);
  }

  @Test
  void shouldThrowDuplicatedEmailException() {
    // given
    final var headmasterId = 157L;
    final var command =
        new HeadmasterCommand("Duplicated", "Email", "741236985", "duplicated@email.com.pl");
    final var headMaster = provideHeadmasterEntity(headmasterId, 345L);

    given(headmasterRepository.findById(headmasterId)).willReturn(of(headMaster));
    given(applicationUserService.existsByEmail(command.email())).willReturn(true);
    // when
    final var exception =
        assertThrows(
            DuplicatedApplicationUserEmailException.class,
            () -> headmasterService.updateById(headmasterId, command));
    // then
    assertThat(exception.getDisplayMessage())
        .isEqualTo("Email: duplicated@email.com.pl already exists in system");
    assertThat(exception.getCode()).isEqualTo("DUPLICATED_EMAIL");
  }

  @ParameterizedTest
  @ValueSource(longs = {549L, 210L})
  void shouldDeleteHeadmaster(long id) {
    // given
    final var applicationUserEntity = mock(ApplicationUserEntity.class);
    final var headmaster = new HeadmasterEntity();
    headmaster.setApplicationUser(applicationUserEntity);
    headmaster.setId(id);

    given(headmasterRepository.findById(id)).willReturn(of(headmaster));
    // when
    headmasterService.deleteById(id);
    // then
    verify(applicationUserEntity, times(1)).setExpired(true);
  }
}
