package pl.com.schoolsystem.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import pl.com.schoolsystem.teacher.BaseIntegrationTestAsTeacher;
import pl.com.schoolsystem.teacher.TeacherCommand;

public class StudentControllerAsTeacherTest extends BaseIntegrationTestAsTeacher {

  @Test
  @SneakyThrows
  public void shouldAddNewStudent() {
    // given
    final var requestBody =
        new StudentCommand("Trzezwy", "Student", "546381729", "trzezwy@student.com.pl");
    // when
    mvc.perform(
            post("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Trzezwy"))
        .andExpect(jsonPath("$.lastName").value("Student"))
        .andExpect(jsonPath("$.email").value("trzezwy@student.com.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("546381729"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 1");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Trzezwy")
        .containsEntry("last_name", "Student")
        .containsEntry("phone_number", "546381729")
        .containsEntry("email", "trzezwy@student.com.pl")
        .containsEntry("role", "STUDENT")
        .containsKey("password")
        .isNotNull();

    final var headmasterEntity =
        jdbcTemplate.queryForMap("select * from student where application_user_id = 1");
    assertThat(headmasterEntity.containsKey("id")).isTrue();
  }

  @Test
  @SneakyThrows
  public void shouldNotCreateStudentWithExistingEmail() {
    // given
    final var requestBody = new StudentCommand("Już", "istnieje", "789123546", "Admin@admin.pl");
    // when
    mvc.perform(
            post("/v1/students")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: Admin@admin.pl already exists in system"));
  }

  @Test
  @SneakyThrows
  public void shouldFailValidationOnPostMethod() {
    // given
    final var requestBody = new TeacherCommand("878451232", "--8-", "Student", "chyba niepoprawny");
    // when
    mvc.perform(
            post("/v1/students")
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
                    "firstName", "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(
            jsonPath(
                "$.details",
                hasEntry(
                    "lastName", "Invalid characters. Name can have only letters, space and dash")))
        .andExpect(
            jsonPath(
                "$.details", hasEntry("phoneNumber", "Phone number must have exactly 9 digits")))
        .andExpect(jsonPath("$.details", hasEntry("email", "Email has bad format")));
  }
}
