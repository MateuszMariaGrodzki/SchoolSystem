package pl.com.schoolsystem.security.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.teacher.BaseIntegrationTestAsTeacher;

public class ApplicationUserControllerAsTeacherTest extends BaseIntegrationTestAsTeacher {

  @Test
  @SneakyThrows
  public void shouldChangeAdministratorPassword() {
    // given
    final var requestBody = new ChangePasswordCommand("Avocado1!", "Teacher867%", "Teacher867%");
    final var encryptedPasswordBeforeRequest =
        jdbcTemplate.queryForObject(
            "Select password from application_user where email = 'teacher@gruszka.pl'",
            String.class);

    // when
    mvc.perform(
            post("/v1/passwords/change")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isOk());

    final var encryptedPasswordAfterRequest =
        jdbcTemplate.queryForObject(
            "select password from application_user where email = 'teacher@gruszka.pl'",
            String.class);
    assertThat(encryptedPasswordBeforeRequest).isNotEqualTo(encryptedPasswordAfterRequest);
  }

  @Test
  @SneakyThrows
  public void shouldFailValidation() {
    // given
    final var requestBody = new ChangePasswordCommand("Avocado1!", null, "Administrator1!");
    // when
    mvc.perform(
            post("/v1/passwords/change")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value("Invalid request"))
        .andExpect(jsonPath("$.details", hasEntry("newPassword", "new password is mandatory")));
  }

  @Test
  @SneakyThrows
  public void shouldFailPasswordValidationInValidator() {
    // given
    final var requestBody = new ChangePasswordCommand("gfda1!", "Headmaster61!", "Headmaster62!");
    // when
    mvc.perform(
            post("/v1/passwords/change")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_EXCEPTION"))
        .andExpect(jsonPath("$.displayMessage").value("password validation failed"))
        .andExpect(
            jsonPath("$.details", hasEntry("new password and retyped password", "doesn't match")))
        .andExpect(jsonPath("$.details", hasEntry("old password", "is incorrect")));
  }
}
