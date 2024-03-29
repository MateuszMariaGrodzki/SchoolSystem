package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.com.schoolsystem.school.SchoolLevel.HIGH;
import static pl.com.schoolsystem.school.SchoolLevel.PRIMARY;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.school.AddressCommand;
import pl.com.schoolsystem.school.SchoolCommand;
import pl.com.schoolsystem.security.user.UserCommand;

public class HeadmasterControllerAsHeadmasterTest extends BaseIntegrationTestAsHeadmaster {

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new HeadmasterCommand(
            new UserCommand("Już", "istnieje", "789123546", "Admin@admin.pl"),
            new SchoolCommand("Liceum", HIGH, new AddressCommand("Lublin", "Zana", "88-666", "8")));
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldGetHeadmasterDataInGetMethod() {
    // given
    final var headmasterId = 321L;
    // when
    mvc.perform(get(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.headmaster.firstName").value("Head"))
        .andExpect(jsonPath("$.headmaster.lastName").value("Master"))
        .andExpect(jsonPath("$.headmaster.email").value("head@master.pl"))
        .andExpect(jsonPath("$.headmaster.phoneNumber").value("111111111"))
        .andExpect(jsonPath("$.headmaster.id").value(headmasterId))
        .andExpect(jsonPath("$.school.id").value(headmasterId))
        .andExpect(jsonPath("$.school.name").value("Liceum imienia Kopernika"))
        .andExpect(jsonPath("$.school.tier").value("HIGH"))
        .andExpect(jsonPath("$.school.city").value("Lublin"))
        .andExpect(jsonPath("$.school.building").value("8/1"))
        .andExpect(jsonPath("$.school.street").value("Mickiewicza"))
        .andExpect(jsonPath("$.school.postCode").value("88-666"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateHeadmasterWhenEmailDoseNotChange() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new UpdateHeadmasterCommand(
            new UserCommand("Update", "headmaster", "666666666", "head@master.pl"));

    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(headmasterId))
        .andExpect(jsonPath("$.firstName").value("Update"))
        .andExpect(jsonPath("$.lastName").value("headmaster"))
        .andExpect(jsonPath("$.phoneNumber").value("666666666"))
        .andExpect(jsonPath("$.email").value("head@master.pl"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 741");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Update")
        .containsEntry("last_name", "headmaster")
        .containsEntry("phone_number", "666666666")
        .containsEntry("email", "head@master.pl")
        .containsEntry("role", "HEADMASTER")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnDeleteMethod() {
    // given
    final var headmasterId = 321L;
    // when
    mvc.perform(delete(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateSchoolData() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new SchoolCommand(
            "Updated data", PRIMARY, new AddressCommand("Kraków", "Krakowska", "00-000", "88/14"));
    // when
    mvc.perform(
            put(format("/v1/headmasters/%s/school", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(headmasterId))
        .andExpect(jsonPath("$.name").value("Updated data"))
        .andExpect(jsonPath("$.tier").value("PRIMARY"))
        .andExpect(jsonPath("$.city").value("Kraków"))
        .andExpect(jsonPath("$.building").value("88/14"))
        .andExpect(jsonPath("$.street").value("Krakowska"))
        .andExpect(jsonPath("$.postCode").value("00-000"));

    final var schoolEntity =
        jdbcTemplate.queryForMap(
            format("select * from school where headmaster_id = %s", headmasterId));
    assertThat(schoolEntity)
        .containsEntry("name", "Updated data")
        .containsEntry("tier", "PRIMARY")
        .containsEntry("city", "Kraków")
        .containsEntry("street", "Krakowska")
        .containsEntry("post_code", "00-000")
        .containsEntry("building", "88/14");
  }

  @Test
  @SneakyThrows
  public void shouldThrowHeadmasterNotFoundExceptionWhenTryingToUpdateAnotherHeadmasterSchool() {
    // given
    final var headmasterId = 322L;
    final var requestBody =
        new SchoolCommand(
            "Updated data", PRIMARY, new AddressCommand("Kraków", "Krakowska", "00-000", "88/14"));
    // when
    mvc.perform(
            put(format("/v1/headmasters/%s/school", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Headmaster with id 322 not found"));
  }
}
