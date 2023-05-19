package pl.com.schoolsystem.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseIntegrationTest extends TestContainers {

  @Autowired protected MockMvc mvc;

  @Autowired protected JdbcTemplate jdbcTemplate;

  @Autowired protected ObjectMapper objectMapper;
}
