package pl.com.schoolsystem.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Tag("integration")
public class Containers extends PostgreSQLContainer<Containers> {

  private static Containers container;

  public static GenericContainer greenMailContainer;

  public Containers() {
    super("postgres:13.1-alpine");
  }

  public static Containers getInstance() {
    if (container == null) {
      container = new Containers();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("DB_URL", container.getJdbcUrl());
    System.setProperty("DB_USERNAME", container.getUsername());
    System.setProperty("DB_PASSWORD", container.getPassword());
  }

  @Override
  public void stop() {}

  @BeforeAll
  public static void init() {
    greenMailContainer =
        new GenericContainer<>(DockerImageName.parse("greenmail/standalone:1.6.1"))
            .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
            .withEnv(
                "GREENMAIL_OPTS",
                "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=mateusz:password")
            .withExposedPorts(3025);
    greenMailContainer.start();
  }

  @DynamicPropertySource
  static void configureMailHost(DynamicPropertyRegistry registry) {
    registry.add("spring.mail.host", greenMailContainer::getHost);
    registry.add("spring.mail.port", greenMailContainer::getFirstMappedPort);
  }
}
