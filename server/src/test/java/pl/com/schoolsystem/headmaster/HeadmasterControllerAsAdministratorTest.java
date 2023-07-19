package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.admin.BaseIntegrationTestAsAdministrator;

public class HeadmasterControllerAsAdministratorTest extends BaseIntegrationTestAsAdministrator {

  @Test
  @SneakyThrows
  public void shouldAddNewHeadmaster() {
    // given
    final var requestBody =
        new HeadmasterCommand("Head", "Master", "874123695", "head@master.com.pl");
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Head"))
        .andExpect(jsonPath("$.lastName").value("Master"))
        .andExpect(jsonPath("$.email").value("head@master.com.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("874123695"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 1");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Head")
        .containsEntry("last_name", "Master")
        .containsEntry("phone_number", "874123695")
        .containsEntry("email", "head@master.com.pl")
        .containsEntry("role", "HEADMASTER")
        .containsKey("password")
        .isNotNull();

    final var headmasterEntity =
        jdbcTemplate.queryForMap("select * from headmaster where application_user_id = 1");
    assertThat(headmasterEntity.containsKey("administrator_id")).isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotAddHeadmasterWithExistingEmail() {
    // given
    final var requestBody = new HeadmasterCommand("Już", "istnieje", "789123546", "Admin@admin.pl");
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: Admin@admin.pl already exists in system"));
  }

  @Test
  @SneakyThrows
  public void shouldFailValidation() {
    // given
    final var requestBody = new HeadmasterCommand("4564655", "", "78", "fkdjgkld");
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value("Invalid request"))
        .andExpect(
            jsonPath(
                "$.details",
                hasEntry(
                    "firstName", "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(jsonPath("$.details", hasEntry("lastName", "Last name is mandatory")))
        .andExpect(
            jsonPath(
                "$.details", hasEntry("phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("email", "Email has bad format")));
  }

  @Test
  @SneakyThrows
  public void shouldGetHeadmasterDataById() {
    // given
    final var headmasterId = 321L;
    // when
    mvc.perform(get(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Head"))
        .andExpect(jsonPath("$.lastName").value("Master"))
        .andExpect(jsonPath("$.email").value("head@master.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("111111111"))
        .andExpect(jsonPath("$.id").value(headmasterId));
  }

  @Test
  @SneakyThrows
  public void shouldThrowHeadMasterNotFoundException() {
    // given
    final var notExistingHeadmasterId = 105L;
    // when
    mvc.perform(get(format("/v1/headmasters/%s", notExistingHeadmasterId)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Headmaster with id 105 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateHeadmaster() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new HeadmasterCommand("UpdatedHead", "UpdatedMaster", "741236985", "UpdatedMaster@com.pl");
    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(headmasterId))
        .andExpect(jsonPath("$.firstName").value("UpdatedHead"))
        .andExpect(jsonPath("$.lastName").value("UpdatedMaster"))
        .andExpect(jsonPath("$.phoneNumber").value("741236985"))
        .andExpect(jsonPath("$.email").value("UpdatedMaster@com.pl"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 741");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "UpdatedHead")
        .containsEntry("last_name", "UpdatedMaster")
        .containsEntry("phone_number", "741236985")
        .containsEntry("email", "UpdatedMaster@com.pl")
        .containsEntry("role", "HEADMASTER")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldFailValidationInUpdate() {
    // given
    final var headmasterId = 321L;
    final var requestBody = new HeadmasterCommand("09323Ksa32!!", "    ", "145", "sd@a");

    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value("Invalid request"))
        .andExpect(
            jsonPath(
                "$.details",
                hasEntry(
                    "firstName", "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(jsonPath("$.details", hasEntry("lastName", "Last name is mandatory")))
        .andExpect(
            jsonPath(
                "$.details", hasEntry("phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("email", "Email has bad format")));
  }

  @Test
  @SneakyThrows
  public void shouldNotUpdateHeadmasterWhenThereIsAnotherUserWithGivenEmailInDatabase() {
    // given
    final var headmasterId = 321L;
    final var requestBody = new HeadmasterCommand("Wrong", "Email", "741236985", "Admin@admin.pl");

    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: Admin@admin.pl already exists in system"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 741");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Head")
        .containsEntry("last_name", "Master")
        .containsEntry("phone_number", "111111111")
        .containsEntry("email", "head@master.pl")
        .containsEntry("role", "HEADMASTER")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldUpdateHeadmasterWhenEmailDoseNotChange() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new HeadmasterCommand("Update", "headmaster", "666666666", "head@master.pl");

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
}
