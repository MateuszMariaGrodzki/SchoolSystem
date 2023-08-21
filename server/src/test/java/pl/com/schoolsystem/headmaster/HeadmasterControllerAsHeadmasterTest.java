package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.com.schoolsystem.school.SchoolLevel.HIGH;

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
        .andExpect(jsonPath("$.headmaster.id").value(headmasterId));
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
}
