package pl.com.schoolsystem.base;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
@Tag("integration")
public class TestContainers {

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

  //  @RegisterExtension
  //  static GreenMailExtension greenMail =
  //      new GreenMailExtension(ServerSetupTest.SMTP)
  //          .withConfiguration(GreenMailConfiguration.aConfig().withUser("mateusz", "password"))
  //          .withPerMethodLifecycle(false);

  @Container
  static GenericContainer greenMailContainer =
      new GenericContainer<>(DockerImageName.parse("greenmail/standalone:1.6.1"))
          .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
          .withEnv(
              "GREENMAIL_OPTS",
              "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=mateusz:password")
          .withExposedPorts(3025);

  @DynamicPropertySource
  static void configureMailHost(DynamicPropertyRegistry registry) {
    registry.add("spring.mail.host", greenMailContainer::getHost);
    registry.add("spring.mail.port", greenMailContainer::getFirstMappedPort);
  }
}
