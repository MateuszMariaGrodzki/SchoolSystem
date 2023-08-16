package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.com.schoolsystem.school.SchoolLevel.HIGH;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.school.AddressCommand;
import pl.com.schoolsystem.school.SchoolCommand;
import pl.com.schoolsystem.security.user.UserCommand;
import pl.com.schoolsystem.teacher.BaseIntegrationTestAsTeacher;

public class HeadmasterControllerAsTeacherTest extends BaseIntegrationTestAsTeacher {

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new HeadmasterCommand(
            new UserCommand("Head", "Master", "456731928", "nowy@headmaster.com"),
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
}
