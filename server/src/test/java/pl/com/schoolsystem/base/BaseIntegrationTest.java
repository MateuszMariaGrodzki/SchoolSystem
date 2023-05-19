package pl.com.schoolsystem.base;

import static org.testcontainers.ext.ScriptUtils.runInitScript;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseIntegrationTest extends TestContainers {

  @Autowired protected MockMvc mvc;

  @Autowired protected JdbcTemplate jdbcTemplate;

  @Autowired protected ObjectMapper objectMapper;

  private static final JdbcDatabaseDelegate DATABASE_DELEGATE =
      new JdbcDatabaseDelegate(postgresSQLContainer, "");

  public void createDropDatabase() {
    runScript("delete.sql");
    runScript("init.sql");
  }

  public void runScript(String name) {
    runInitScript(DATABASE_DELEGATE, name);
  }
}
