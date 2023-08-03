package pl.com.schoolsystem.student;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class StudentControllerAsStudentTest extends BaseIntegrationTestAsStudent {

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

  @Test
  @SneakyThrows
  public void shouldUpdateStudent() {
    // given
    final var studentId = 4786L;
    final var requestBody =
        new StudentCommand("Updatable", "Student-student", "741982365", "updated@student.com.pl");
    // when
    mvc.perform(
            put(format("/v1/students/%s", studentId))
                .content(objectMapper.writeValueAsString(requestBody))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(4786))
        .andExpect(jsonPath("$.firstName").value("Updatable"))
        .andExpect(jsonPath("$.lastName").value("Student-student"))
        .andExpect(jsonPath("$.email").value("updated@student.com.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("741982365"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 876");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Updatable")
        .containsEntry("last_name", "Student-student")
        .containsEntry("phone_number", "741982365")
        .containsEntry("email", "updated@student.com.pl")
        .containsEntry("role", "STUDENT")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenInDeleteMethod() {
    // given
    final var studentId = 4786L;
    // when
    mvc.perform(delete(format("/v1/students/%s", studentId)))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }
}
