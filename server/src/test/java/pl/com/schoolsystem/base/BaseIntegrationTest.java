package pl.com.schoolsystem.base;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
  @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:init.sql"),
  @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:delete.sql")
})
public class BaseIntegrationTest extends Containers {

  @Autowired protected MockMvc mvc;

  @Autowired protected JdbcTemplate jdbcTemplate;

  @Autowired protected ObjectMapper objectMapper;
}
