package pl.com.schoolsystem.headmaster;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class HeadmasterControllerAsHeadmasterTest extends BaseIntegrationTestAsHeadmaster {

  @Test
  @SneakyThrows
  public void shouldGetForbiddenOnPostHeadmasterMethod() {
    // given
    final var requestBody = new HeadmasterCommand("Już", "istnieje", "789123546", "Admin@admin.pl");
    // when
    mvc.perform(
            post("/v1/headmasters")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(APPLICATION_JSON))
        // then
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("Acces is denied"));
  }

  @Test
  @SneakyThrows
  public void shouldGetHeadmasterDataInGetMethod() {
    // given
    final var headmasterId = 321L;
    // when
    mvc.perform(get(format("/v1/headmasters/%s", headmasterId)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Head"))
        .andExpect(jsonPath("$.lastName").value("Master"))
        .andExpect(jsonPath("$.email").value("head@master.pl"))
        .andExpect(jsonPath("$.phoneNumber").value("111111111"))
        .andExpect(jsonPath("$.id").value(headmasterId));
  }
}
