package pl.com.schoolsystem.security.user;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pl.com.schoolsystem.admin.BaseIntegrationTestAsAdministrator;

public class ApplicationUserControllerAsAdministratorTest
    extends BaseIntegrationTestAsAdministrator {

  @Test
  @SneakyThrows
  public void shouldChangeAdministratorPassword() {
    // given
    final var requestBody =
        new ChangePasswordCommand("Avocado1!", "Administrator1!", "Administrator1!");
    final var encryptedPasswordBeforeRequest =
        jdbcTemplate.queryForObject(
            "Select password from application_user where email = 'Admin@admin.pl'", String.class);

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
            "select password from application_user where email = 'Admin@admin.pl'", String.class);
    assertThat(encryptedPasswordBeforeRequest).isNotEqualTo(encryptedPasswordAfterRequest);
  }
}
