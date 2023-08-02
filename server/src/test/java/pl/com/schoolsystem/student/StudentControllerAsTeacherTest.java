package pl.com.schoolsystem.student;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

  @Test
  @SneakyThrows
  public void shouldFindUserInGetByIdMethod() {
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
  public void shouldThrowStudentNotFoundException() {
    // given
    final var studentId = 549385934L;
    // when
    mvc.perform(get(format("/v1/students/%s", studentId)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Student with id 549385934 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateStudent() {
    // given
    final var studentId = 4786L;
    final var requestBody =
        new StudentCommand("Change", "Student-by-teacher", "888888888", "updated@byTeacher.com.pl");
    // when
    mvc.perform(
            put(format("/v1/students/%s", studentId))
                .content(objectMapper.writeValueAsString(requestBody))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(4786))
        .andExpect(jsonPath("$.firstName").value("Change"))
        .andExpect(jsonPath("$.lastName").value("Student-by-teacher"))
        .andExpect(jsonPath("$.email").value("updated@byTeacher.com.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("888888888"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 876");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Change")
        .containsEntry("last_name", "Student-by-teacher")
        .containsEntry("phone_number", "888888888")
        .containsEntry("email", "updated@byTeacher.com.pl")
        .containsEntry("role", "STUDENT")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldFailValidationInUpdateMethod() {
    // given
    final var studentId = 4786L;
    final var requestBody =
        new StudentCommand(null, "Student!by3teacher", "88/888", "@byTeacher.com.pl");
    // when
    mvc.perform(
            put(format("/v1/students/%s", studentId))
                .content(objectMapper.writeValueAsString(requestBody))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value("Invalid request"))
        .andExpect(jsonPath("$.details", hasEntry("firstName", "First name is mandatory")))
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

  @Test
  @SneakyThrows
  public void shouldThrowUserNotFoundExceptionInUpdateMethod() {
    // given
    final var studentId = 545687999L;
    final var requestBody = new StudentCommand("Mat", "Gro", "789456123", "mat@gro.com.pl");
    // when
    mvc.perform(
            put(format("/v1/students/%s", studentId))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Student with id 545687999 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldThrowDuplicatedEmailExceptionInUpdateMethod() {
    // given
    final var studentId = 4786L;
    final var requestBody = new StudentCommand("Update", "Student", "456654456", "Admin@admin.pl");
    // when
    mvc.perform(
            put(format("/v1/students/%s", studentId))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: Admin@admin.pl already exists in system"));
  }
}
