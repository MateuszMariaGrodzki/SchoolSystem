package pl.com.schoolsystem.teacher;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.security.user.UserCommand;

public class TeacherControllerAsTeacherTest extends BaseIntegrationTestAsTeacher {

  @Test
  @SneakyThrows
  public void shouldReturnForbiddenOnPostMethod() {
    // given
    final var requestBody =
        new TeacherCommand(new UserCommand("Teacher", "Teacher", "741236985", "teb@com.pl"));
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

  @Test
  @SneakyThrows
  public void shouldUpdateTeacher() {
    // given
    final var teacherId = 86L;
    final var requestBody =
        new TeacherCommand(
            new UserCommand("UpTeacher", "Grade", "565656987", "upgraded@teacher.com.pl"));
    // when
    mvc.perform(
            put(format("/v1/teachers/%s", teacherId))
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(teacherId))
        .andExpect(jsonPath("$.firstName").value("UpTeacher"))
        .andExpect(jsonPath("$.lastName").value("Grade"))
        .andExpect(jsonPath("$.phoneNumber").value("565656987"))
        .andExpect(jsonPath("$.email").value("upgraded@teacher.com.pl"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 56");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "UpTeacher")
        .containsEntry("last_name", "Grade")
        .containsEntry("phone_number", "565656987")
        .containsEntry("email", "upgraded@teacher.com.pl")
        .containsEntry("role", "TEACHER")
        .containsKey("password")
        .isNotNull();
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
