package pl.com.schoolsystem.admin;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.headmaster.BaseIntegrationTestAsHeadmaster;
import pl.com.schoolsystem.security.user.UserCommand;

public class AdministratorControllerAsHeadmasterTest extends BaseIntegrationTestAsHeadmaster {

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new AdministratorCommand(new UserCommand("Admin", "Nowy", "789456132", "nowy@admin.com"));
    // when
    mvc.perform(
            post("/v1/administrators")
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
  public void shouldGetForbiddenOnGetMethod() {
    // given
    final var administratorId = 40532;
    // when
    mvc.perform(get(format("/v1/administrators/%s", administratorId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPutMethod() {
    // given
    final var administratorId = 40532;
    final var requestBody =
        new AdministratorCommand(new UserCommand("Admin", "Nowy", "789456132", "nowy@admin.com"));
    // when
    mvc.perform(
            put(format("/v1/administrators/%s", administratorId))
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
  public void shouldGetForbiddenOnDeleteMethod() {
    // given
    final var administratorId = 40532;
    // when
    mvc.perform(delete(format("/v1/administrators/%s", administratorId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }
}
