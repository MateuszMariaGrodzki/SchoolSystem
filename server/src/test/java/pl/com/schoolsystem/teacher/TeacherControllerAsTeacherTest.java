package pl.com.schoolsystem.teacher;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class TeacherControllerAsTeacherTest extends BaseIntegrationTestAsTeacher {

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnPostMethod() {
    // given
    final var requestBody = new TeacherCommand("Teacher", "Teacher", "741236985", "teb@com.pl");
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
  public void shouldGetTeacherDataInGetByIdMethod() {
    // given
    final var teacherId = 86L;
    // when
    mvc.perform(get(format("/v1/teachers/%s", teacherId)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(teacherId))
        .andExpect(jsonPath("$.firstName").value("Teacher"))
        .andExpect(jsonPath("$.lastName").value("Gruszka"))
        .andExpect(jsonPath("$.email").value("teacher@gruszka.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("222222222"));
  }
}
