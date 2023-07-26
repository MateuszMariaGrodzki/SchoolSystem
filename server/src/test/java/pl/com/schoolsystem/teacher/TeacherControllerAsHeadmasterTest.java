package pl.com.schoolsystem.teacher;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.headmaster.BaseIntegrationTestAsHeadmaster;

public class TeacherControllerAsHeadmasterTest extends BaseIntegrationTestAsHeadmaster {

  @Test
  @SneakyThrows
  public void shouldAddNewTeacher() {
    // given
    final var requestBody =
        new TeacherCommand("Teacher", "Teacherowski", "456123789", "teacher@onet.pl");
    // when
    mvc.perform(
            post("/v1/teachers")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Teacher"))
        .andExpect(jsonPath("$.lastName").value("Teacherowski"))
        .andExpect(jsonPath("$.email").value("teacher@onet.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("456123789"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 1");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "Teacher")
        .containsEntry("last_name", "Teacherowski")
        .containsEntry("phone_number", "456123789")
        .containsEntry("email", "teacher@onet.pl")
        .containsEntry("role", "TEACHER")
        .containsKey("password")
        .isNotNull();

    final var headmasterEntity =
        jdbcTemplate.queryForMap("select * from teacher where application_user_id = 1");
    assertThat(headmasterEntity.containsKey("id")).isTrue();
  }

  @Test
  @SneakyThrows
  public void shouldNotCreateTeacherWithExistingEmail() {
    // given
    final var requestBody = new TeacherCommand("Już", "istnieje", "789123546", "Admin@admin.pl");
    // when
    mvc.perform(
            post("/v1/teachers")
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
    final var requestBody = new TeacherCommand(null, "BOM\\uFeFF", "74136985a", "no nie wiem");
    // when
    mvc.perform(
            post("/v1/teachers")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
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
  public void shouldGetTeacherDataFromGetByIdMethod() {
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
  public void shouldReturnThrowTeacherNotFoundException() {
    // given
    final var teacherId = 6584694L;
    // when
    mvc.perform(get(format("/v1/teachers/%s", teacherId)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Teacher with id 6584694 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldUpdateTeacher() {
    // given
    final var teacherId = 86L;
    final var requestBody =
        new TeacherCommand("UpdateTea", "Updatecher", "545454123", "massive@upgrade.com.pl");
    // when
    mvc.perform(
            put(format("/v1/teachers/%s", teacherId))
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(teacherId))
        .andExpect(jsonPath("$.firstName").value("UpdateTea"))
        .andExpect(jsonPath("$.lastName").value("Updatecher"))
        .andExpect(jsonPath("$.phoneNumber").value("545454123"))
        .andExpect(jsonPath("$.email").value("massive@upgrade.com.pl"));

    final var applicationUserEntity =
        jdbcTemplate.queryForMap("select * from application_user where id = 56");
    assertThat(applicationUserEntity)
        .containsEntry("first_name", "UpdateTea")
        .containsEntry("last_name", "Updatecher")
        .containsEntry("phone_number", "545454123")
        .containsEntry("email", "massive@upgrade.com.pl")
        .containsEntry("role", "TEACHER")
        .containsKey("password")
        .isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldFailValidationInUpdate() {
    // given
    final var teacherId = 86L;
    final var requestBody =
        new TeacherCommand(null, "5834g3834-", "babajaga2", "@babajaga.kwejk.pl");
    // when
    mvc.perform(
            put(format("/v1/teachers/%s", teacherId))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
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
  public void shouldThrowTeacherNotFoundInUpdateMethod() {
    // given
    final var teacherId = 184L;
    final var requestBody =
        new TeacherCommand("Good", "Data", "789456123", "teacher@teacher.com.pl");
    // when
    mvc.perform(
            put(format("/v1/teachers/%s", teacherId))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Teacher with id 184 not found"));
  }

  @Test
  @SneakyThrows
  public void shouldThrowDuplicatedEmailExceptionInUpdateMethod() {
    // given
    final var teacherId = 86L;
    final var requestBody =
        new TeacherCommand("Teacher", "ToUpdate", "741258963", "Admin@admin.pl");
    // when
    mvc.perform(
            put(format("/v1/teachers/%s", teacherId))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
        .andExpect(jsonPath("$.message").value("Email: Admin@admin.pl already exists in system"));
  }
}
