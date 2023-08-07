package pl.com.schoolsystem.teacher;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.admin.BaseIntegrationTestAsAdministrator;
import pl.com.schoolsystem.security.user.UserCommand;

public class TeacherControllerAsAdministratorTest extends BaseIntegrationTestAsAdministrator {

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new TeacherCommand(new UserCommand("Teacher", "First", "852369741", "teacher@first.pl"));
    // when
    mvc.perform(
            post("/v1/teachers")
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
  public void shouldReturnForbiddenOnGetMethod() {
    // given
    final var teacherId = 86L;
    // when
    mvc.perform(get(format("/v1/teachers/%s", teacherId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnUpdateMethod() {
    // given
    final var teacherId = 86L;
    final var requestBody =
        new TeacherCommand(
            new UserCommand("Update", "Teacher", "456123789", "teacher@update.com.pl"));
    // when
    mvc.perform(
            put(format("/v1/teachers/%s", teacherId))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnDeleteMethod() {
    // given
    final var teacherId = 86L;
    // when
    mvc.perform(delete(format("/v1/teachers/%s", teacherId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }
}
