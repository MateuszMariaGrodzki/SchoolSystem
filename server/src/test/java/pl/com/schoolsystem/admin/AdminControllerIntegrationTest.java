package pl.com.schoolsystem.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest extends BaseIntegrationTestAsAdmin {

  @Autowired private MockMvc mockMvc;

  @Test
  @SneakyThrows
  public void test24() {
    mockMvc.perform(post("/admins")).andExpect(status().isOk());
  }
}
