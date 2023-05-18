package pl.com.schoolsystem.base;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Tag("integration")
public class PostgresSqlTestContainer {

  @Container
  public static PostgreSQLContainer<?> postgresSQLContainer =
      new PostgreSQLContainer<>("postgres:13.1-alpine")
          .withDatabaseName("schoolsystem")
          .withUsername("schoolsystem")
          .withPassword("schoolsystem");

  @DynamicPropertySource
  public static void containerConfig(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
  }

  @RegisterExtension
  static GreenMailExtension greenMail =
      new GreenMailExtension(ServerSetupTest.SMTP)
          .withConfiguration(GreenMailConfiguration.aConfig().withUser("mateusz", "password"))
          .withPerMethodLifecycle(false);
}
