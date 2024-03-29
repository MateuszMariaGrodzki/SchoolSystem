package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.com.schoolsystem.school.SchoolLevel.HIGH;
import static pl.com.schoolsystem.school.SchoolLevel.PRIMARY;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.admin.BaseIntegrationTestAsAdministrator;
import pl.com.schoolsystem.school.AddressCommand;
import pl.com.schoolsystem.school.SchoolCommand;
import pl.com.schoolsystem.security.authentication.AuthCommand;
import pl.com.schoolsystem.security.user.UserCommand;

public class HeadmasterControllerAsAdministratorTest extends BaseIntegrationTestAsAdministrator {

  @Test
  @SneakyThrows
  public void shouldAddNewHeadmaster() {
    // given
    final var requestBody =
        new HeadmasterCommand(
            new UserCommand("Head", "Master", "874123695", "head@master.com.pl"),
            new SchoolCommand(
                "Liceum Kopernika", HIGH, new AddressCommand("Lublin", "Lipowa", "20-555", "8")));
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.headmaster.firstName").value("Head"))
        .andExpect(jsonPath("$.headmaster.lastName").value("Master"))
        .andExpect(jsonPath("$.headmaster.email").value("head@master.com.pl"))
        .andExpect(jsonPath("$.headmaster.phoneNumber").value("874123695"))
        .andExpect(jsonPath("$.school.name").value("Liceum Kopernika"))
        .andExpect(jsonPath("$.school.tier").value("HIGH"))
        .andExpect(jsonPath("$.school.city").value("Lublin"))
        .andExpect(jsonPath("$.school.street").value("Lipowa"))
        .andExpect(jsonPath("$.school.postCode").value("20-555"))
        .andExpect(jsonPath("$.school.building").value("8"));

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

    final var schoolEntity = jdbcTemplate.queryForMap("select * from school where id = 1");
    assertThat(schoolEntity)
        .containsEntry("name", "Liceum Kopernika")
        .containsEntry("tier", "HIGH")
        .containsEntry("city", "Lublin")
        .containsEntry("street", "Lipowa")
        .containsEntry("post_code", "20-555")
        .containsEntry("building", "8")
        .containsEntry("headmaster_id", 1L);

    final var headmasterEntity =
        jdbcTemplate.queryForMap("select * from headmaster where application_user_id = 1");
    assertThat(headmasterEntity.containsKey("administrator_id")).isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotAddHeadmasterWithExistingEmail() {
    // given
    final var requestBody =
        new HeadmasterCommand(
            new UserCommand("Już", "istnieje", "789123546", "admin@admin.pl"),
            new SchoolCommand(
                "Liceum kopernika", HIGH, new AddressCommand("Lublin", "Lipowa", "88-666", "81")));
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: admin@admin.pl already exists in system"));
  }

  @Test
  @SneakyThrows
  public void shouldFailValidation() {
    // given
    final var requestBody =
        new HeadmasterCommand(
            new UserCommand("4564655", "", "78", "fkdjgkld"),
            new SchoolCommand(null, null, new AddressCommand("", null, "dsaasd", "81")));
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
                    "personalData.firstName",
                    "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(
            jsonPath("$.details", hasEntry("personalData.lastName", "Last name is mandatory")))
        .andExpect(
            jsonPath(
                "$.details",
                hasEntry("personalData.phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("personalData.email", "Email has bad format")))
        .andExpect(jsonPath("$.details", hasEntry("schoolData.name", "School name is mandatory")))
        .andExpect(jsonPath("$.details", hasEntry("schoolData.tier", "School tier is mandatory")))
        .andExpect(jsonPath("$.details", hasEntry("schoolData.address.city", "City is mandatory")))
        .andExpect(
            jsonPath("$.details", hasEntry("schoolData.address.street", "Street is mandatory")))
        .andExpect(
            jsonPath(
                "$.details", hasEntry("schoolData.address.postCode", "Post code has bad pattern")));
  }

  @Test
  @SneakyThrows
  public void
      shouldThrowValidationExceptionWhenUserCommandIsNotPresentAndSchoolCommandIsNotPresent() {
    // given
    final var requestBody = new HeadmasterCommand(null, null);
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
            jsonPath("$.details", hasEntry("personalData", "user personal data is mandatory")))
        .andExpect(jsonPath("$.details", hasEntry("schoolData", "school data is mandatory")));
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
  public void shouldThrowHeadmasterNotFoundExceptionOnDeletedHeadmaster() {
    // given
    final var deletedHeadmasterId = 452L;
    // when
    mvc.perform(get(format("/v1/headmasters/%s", deletedHeadmasterId)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Headmaster with id 452 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateHeadmaster() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new UpdateHeadmasterCommand(
            new UserCommand("UpdatedHead", "UpdatedMaster", "741236985", "UpdatedMaster@com.pl"));
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
    final var requestBody =
        new UpdateHeadmasterCommand(new UserCommand("09323Ksa32!!", "    ", "145", "sd@a"));

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
                    "personalData.firstName",
                    "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(
            jsonPath("$.details", hasEntry("personalData.lastName", "Last name is mandatory")))
        .andExpect(
            jsonPath(
                "$.details",
                hasEntry("personalData.phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("personalData.email", "Email has bad format")));
  }

  @Test
  @SneakyThrows
  public void shouldNotUpdateHeadmasterWhenThereIsAnotherUserWithGivenEmailInDatabase() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new UpdateHeadmasterCommand(
            new UserCommand("Wrong", "Email", "741236985", "admin@admin.pl"));

    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", headmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: admin@admin.pl already exists in system"));

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
  public void shouldNotUpdateDeletedHeadmaster() {
    // given
    final var deletedHeadmasterId = 452L;
    final var requestBody =
        new UpdateHeadmasterCommand(
            new UserCommand("Update", "headmaster", "666666666", "head@master.pl"));
    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", deletedHeadmasterId))
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Headmaster with id 452 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldDeleteHeadmaster() {
    // given
    final var headmasterId = 321L;
    // when
    mvc.perform(delete(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isNoContent());

    final var isExpiredFlag =
        jdbcTemplate.queryForObject(
            "select u.is_expired from application_user u where u.id = 741", Boolean.class);
    assertThat(isExpiredFlag).isTrue();
  }

  @Test
  @SneakyThrows
  public void shouldReturnNotContentOnNotExistingHeadmaster() {
    // given
    final var headmasterId = 7896541L;
    // when
    mvc.perform(delete(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isNoContent());
  }

  @Test
  @SneakyThrows
  public void shouldDeleteHeadmasterAndThenThrowAccountExpiredExceptionOnAuthenticating() {
    // given
    final var headmasterId = 321L;
    final var authCommand = new AuthCommand("head@master.pl", "Avocado1!");
    // when
    mvc.perform(delete(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isNoContent());
    mvc.perform(
            post("/v1/token")
                .content(objectMapper.writeValueAsString(authCommand))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN"))
        .andExpect(jsonPath("$.message").value("Account has been deleted"));
  }

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnUpdateSchool() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new SchoolCommand("Not", PRIMARY, new AddressCommand("Not", "Uodatable", "00-213", "784"));
    // when
    mvc.perform(
            put(format("/v1/headmasters/%s/school", headmasterId))
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }
}
