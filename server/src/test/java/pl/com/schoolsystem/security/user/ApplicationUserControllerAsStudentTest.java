package pl.com.schoolsystem.security.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.student.BaseIntegrationTestAsStudent;

public class ApplicationUserControllerAsStudentTest extends BaseIntegrationTestAsStudent {

  @Test
  @SneakyThrows
  public void shouldChangeStudentPassword() {
    // given
    final var requestBody =
        new ChangePasswordCommand("Avocado1!", "DrunkStudent543@", "DrunkStudent543@");
    final var encryptedPasswordBeforeRequest =
        jdbcTemplate.queryForObject(
            "Select password from application_user where email = 'trzezwy@student.pl'",
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
            "select password from application_user where email = 'trzezwy@student.pl'",
            String.class);
    assertThat(encryptedPasswordBeforeRequest).isNotEqualTo(encryptedPasswordAfterRequest);
  }

  @Test
  @SneakyThrows
  public void shouldFailValidation() {
    // given
    final var requestBody = new ChangePasswordCommand("", "Student123", "");
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
        .andExpect(
            jsonPath(
                "$.details", hasEntry("retypedNewPassword", "retyped new password is mandatory")))
        .andExpect(jsonPath("$.details", hasEntry("newPassword", "Password too weak")))
        .andExpect(jsonPath("$.details", hasEntry("oldPassword", "old password is mandatory")));
  }

  @Test
  @SneakyThrows
  public void shouldFailPasswordValidationInValidator() {
    // given
    final var requestBody = new ChangePasswordCommand("dsadsa!", "Studenciak123@", "Studenciak");
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
