package pl.com.schoolsystem.school;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.com.schoolsystem.school.SchoolLevel.PRIMARY;
import static pl.com.schoolsystem.school.SchoolServiceDataTestFactory.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.com.schoolsystem.headmaster.HeadmasterEntity;

public class SchoolServiceTest {

  private final SchoolRepository schoolRepository = mock(SchoolRepository.class);

  private final SchoolService schoolService = new SchoolService(schoolRepository);

  @Test
  void shouldCreateNewSchool() {
    // given
    final var headmaster = mock(HeadmasterEntity.class);
    final var command =
        new SchoolCommand(
            "Liceum", PRIMARY, new AddressCommand("Lublin", "lubelskiego lipca", "80-666", "70"));
    final ArgumentCaptor<SchoolEntity> schoolCaptor = forClass(SchoolEntity.class);
    final var entity = provideSchoolEntityForPostMethod(70L);

    given(schoolRepository.save(any(SchoolEntity.class))).willReturn(entity);
    // when
    final var result = schoolService.create(headmaster, command);
    // then
    verify(schoolRepository).save(schoolCaptor.capture());

    final var savedSchool = schoolCaptor.getValue();

    final var addressFromCommand = command.address();

    assertThat(result.name()).isEqualTo(command.name());
    assertThat(result.tier()).isEqualTo(command.tier());
    assertThat(result.building()).isEqualTo(addressFromCommand.building());
    assertThat(result.street()).isEqualTo(addressFromCommand.street());
    assertThat(result.city()).isEqualTo(addressFromCommand.city());
    assertThat(result.postCode()).isEqualTo(addressFromCommand.postCode());

    assertThat(savedSchool.getName()).isEqualTo(command.name());
    assertThat(savedSchool.getTier()).isEqualTo(command.tier());

    final var addressFromSavedSchool = savedSchool.getAddress();
    assertThat(addressFromCommand).usingRecursiveComparison().isEqualTo(addressFromSavedSchool);
  }
}
