package pl.com.schoolsystem.student;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.headmaster.BaseIntegrationTestAsHeadmaster;

public class StudentControllerAsHeadmasterTest extends BaseIntegrationTestAsHeadmaster {

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new StudentCommand("Student", "Forbidden", "852369741", "student@first.pl");
    // when
    mvc.perform(
            post("/v1/students")
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
  public void shouldGetStudentDataInGetMethod() {
    // given
    final var studentId = 4786L;
    // when
    mvc.perform(get(format("/v1/students/%s", studentId)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(4786))
        .andExpect(jsonPath("$.firstName").value("Trzezwy"))
        .andExpect(jsonPath("$.lastName").value("Student"))
        .andExpect(jsonPath("$.email").value("trzezwy@student.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("333333333"));
  }
}
