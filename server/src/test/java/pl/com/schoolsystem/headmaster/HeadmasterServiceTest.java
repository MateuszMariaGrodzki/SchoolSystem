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
import pl.com.schoolsystem.security.user.*;

public class HeadmasterServiceTest {

  private final ApplicationUserService applicationUserService = mock(ApplicationUserService.class);

  private final HeadmasterRepository headmasterRepository = mock(HeadmasterRepository.class);

  private final PasswordService passwordService = mock(PasswordService.class);

  private final EmailSender emailSender = mock(EmailSender.class);

  private final EmailValidator emailValidator = mock(EmailValidator.class);

  private final HeadmasterService headmasterService =
      new HeadmasterService(
          applicationUserService,
          headmasterRepository,
          passwordService,
          emailSender,
          emailValidator);

  @Test
  void shouldRegisterNewHeadmaster() {
    // given
    final var command =
        new HeadmasterCommand(new UserCommand("Head", "Master", "794613285", "head@master.pl"));
    final var randomPassword = "lkoiujkioljs";
    final var encodedPassword = "jkdsjflsdjflsdjfskldjfsldfjsldfj";
    final var applicationUserCommand = provideApplicationUserCommandForCreateMethod();
    final var applicationUserEntity =
        provideApplicationUserEntity(command.personalData(), encodedPassword);
    final var headmasterEntity = provideHeadmasterEntity(123L, 245L);
    final var personalData = command.personalData();

    given(passwordService.generateNewRandomPassword()).willReturn(randomPassword);
    given(passwordService.encodePassword(randomPassword)).willReturn(encodedPassword);
    given(applicationUserService.create(applicationUserCommand)).willReturn(applicationUserEntity);
    given(headmasterRepository.save(any())).willReturn(headmasterEntity);
    doNothing().when(emailSender).sendNewUserEmail(any(), any());
    // when
    final var result = headmasterService.create(command);
    // then
    final ArgumentCaptor<HeadmasterEntity> headmasterCaptor = forClass(HeadmasterEntity.class);
    verify(headmasterRepository).save(headmasterCaptor.capture());
    final var savedAdministrator = headmasterCaptor.getValue();

    assertThat(result.id()).isEqualTo(123L);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());

    final var savedApplicationUser = savedAdministrator.getApplicationUser();
    assertThat(savedApplicationUser.getEmail()).isEqualTo(personalData.email());
    assertThat(savedApplicationUser.getFirstName()).isEqualTo(personalData.firstName());
    assertThat(savedApplicationUser.getLastName()).isEqualTo(personalData.lastName());
    assertThat(savedApplicationUser.getPhoneNumber()).isEqualTo(personalData.phoneNumber());
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
        new HeadmasterCommand(
            new UserCommand("UpdatedHead", "UpdatedMaster", "75315991", "head@master.com.pl"));
    final var headmaster = provideHeadmasterEntity(headmasterId, 147L);
    final var personalData = command.personalData();

    given(headmasterRepository.findById(headmasterId)).willReturn(of(headmaster));
    given(
            emailValidator.isEmailUniqueInDatabase(
                headmaster.getApplicationUser(), personalData.email()))
        .willReturn(true);
    // when
    final var result = headmasterService.updateById(headmasterId, command);
    // then
    assertThat(result.phoneNumber()).isEqualTo(personalData.phoneNumber());
    assertThat(result.id()).isEqualTo(headmasterId);
    assertThat(result.email()).isEqualTo(personalData.email());
    assertThat(result.firstName()).isEqualTo(personalData.firstName());
    assertThat(result.lastName()).isEqualTo(personalData.lastName());
  }

  @Test
  void shouldThrowHeadmasterNotFoundExceptionInUpdateMethod() {
    // given
    final var headmasterId = 134L;
    final var command =
        new HeadmasterCommand(
            new UserCommand("Not", "Existing", "789456123", "not@existing.com.pl"));

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
  void shouldThrowDuplicatedEmailException() {
    // given
    final var headmasterId = 157L;
    final var command =
        new HeadmasterCommand(
            new UserCommand("Duplicated", "Email", "741236985", "duplicated@email.com.pl"));
    final var headMaster = provideHeadmasterEntity(headmasterId, 345L);

    given(headmasterRepository.findById(headmasterId)).willReturn(of(headMaster));
    given(
            emailValidator.isEmailUniqueInDatabase(
                headMaster.getApplicationUser(), command.personalData().email()))
        .willReturn(false);
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
