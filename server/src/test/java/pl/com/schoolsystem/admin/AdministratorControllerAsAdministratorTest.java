package pl.com.schoolsystem.admin;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class AdministratorControllerAsAdministratorTest extends BaseIntegrationTestAsAdministrator {

  @Test
  @SneakyThrows
  public void shouldAddNewAdministrator() {
    // given
    final var requestBody =
        new AdministratorCommand(
            "Zdenerwowana", "Agnieszka", "789123546", "zdenerwowana.aga@onet.pl");
    // when
    mvc.perform(
            post("/v1/administrators")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Zdenerwowana"))
        .andExpect(jsonPath("$.lastName").value("Agnieszka"))
        .andExpect(jsonPath("$.email").value("zdenerwowana.aga@onet.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("789123546"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 2");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Zdenerwowana")
        .containsEntry("last_name", "Agnieszka")
        .containsEntry("phone_number", "789123546")
        .containsEntry("email", "zdenerwowana.aga@onet.pl")
        .containsEntry("role", "ADMIN")
        .containsKey("password")
        .isNotNull();

    final var administratorEntity =
        jdbcTemplate.queryForMap("select * from administrator where application_user_id = 2");
    assertThat(administratorEntity.containsKey("administrator_id")).isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotAddAdminWithExistingEmail() {
    // given
    final var requestBody =
        new AdministratorCommand("Już", "istnieje", "789123546", "Admin@admin.pl");
    // when
    mvc.perform(
            post("/v1/administrators")
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
    final var requestBody = new AdministratorCommand("", "Jag123mfds", "159", "Adminadmin.pl");
    // when
    mvc.perform(
            post("/v1/administrators")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value("Invalid request"))
        .andExpect(jsonPath("$.details", hasEntry("firstName", "First name is mandatory")))
        .andExpect(
            jsonPath(
                "$.details",
                hasEntry(
                    "lastName", "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(
            jsonPath(
                "$.details", hasEntry("phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("email", "Email has bad format")));
  }

  @Test
  @SneakyThrows
  public void shouldGetAdministratorData() {
    // given
    final var administratorId = 1;
    // when
    mvc.perform(get(format("/v1/administrators/%s", administratorId)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Admin"))
        .andExpect(jsonPath("$.lastName").value("Admin"))
        .andExpect(jsonPath("$.email").value("Admin@admin.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("000000000"));
  }

  @Test
  @SneakyThrows
  public void shouldNotFindNotExistingAdministrator() {
    // given
    final var administratorId = 254564;
    // when
    mvc.perform(get(format("/v1/administrators/%s", administratorId)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("User with id 254564 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateAdministrator() {
    // given
    final var administratorId = 1;
    final var requestBody =
        new AdministratorCommand("Administrator", "AfterChanges", "999999999", "changed@email.com");
    // when
    mvc.perform(
            put(format("/v1/administrators/%s", administratorId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Administrator"))
        .andExpect(jsonPath("$.lastName").value("AfterChanges"))
        .andExpect(jsonPath("$.email").value("changed@email.com"))
        .andExpect(jsonPath("$.phoneNumber").value("999999999"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 1");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Administrator")
        .containsEntry("last_name", "AfterChanges")
        .containsEntry("phone_number", "999999999")
        .containsEntry("email", "changed@email.com")
        .containsEntry("role", "ADMIN")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldThrowValidationExceptionWhenRequestBodyHasBadDataInUpdateMethod() {
    // given
    final var administratorId = 1;
    final var requestBody = new AdministratorCommand(null, "", "99g99999", "@email.com");
    // when
    mvc.perform(
            put(format("/v1/administrators/%s", administratorId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value("Invalid request"))
        .andExpect(jsonPath("$.details", hasEntry("firstName", "First name is mandatory")))
        .andExpect(jsonPath("$.details", hasEntry("lastName", "Last name is mandatory")))
        .andExpect(
            jsonPath(
                "$.details", hasEntry("phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("email", "Email has bad format")));
  }

  @Test
  @SneakyThrows
  public void shouldReturnBadRequestWhenThereIsAnotherUserWithProvidedEmail() {
    // given
    final var administratorId = 1;
    final var requestBody =
        new AdministratorCommand("Administrator", "AfterChanges", "999999999", "admin@test.pl");
    // when
    mvc.perform(
            put(format("/v1/administrators/%s", administratorId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: admin@test.pl already exists in system"));
  }
}
