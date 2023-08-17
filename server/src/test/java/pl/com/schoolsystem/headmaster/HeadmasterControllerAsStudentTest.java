package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.com.schoolsystem.school.SchoolLevel.HIGH;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.school.AddressCommand;
import pl.com.schoolsystem.school.SchoolCommand;
import pl.com.schoolsystem.security.user.UserCommand;
import pl.com.schoolsystem.student.BaseIntegrationTestAsStudent;

public class HeadmasterControllerAsStudentTest extends BaseIntegrationTestAsStudent {

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new HeadmasterCommand(
            new UserCommand("Head", "master", "789456132", "nowy@head.com"),
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
  public void shouldGetForbiddenOnGetMethod() {
    // given
    final var headmasterId = 321L;
    // when
    mvc.perform(get(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPutMethod() {
    // given
    final var headmasterId = 321L;
    final var requestBody =
        new UpdateHeadmasterCommand(
            new UserCommand("Head", "Updatable", "741258963", "head@master.com"));

    // when
    mvc.perform(
            put(format("/v1/headmasters/%s", headmasterId))
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
    final var headmasterId = 321L;
    // when
    mvc.perform(delete(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }
}
