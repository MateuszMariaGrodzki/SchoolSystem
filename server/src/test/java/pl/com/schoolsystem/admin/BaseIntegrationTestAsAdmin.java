package pl.com.schoolsystem.admin;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.test.context.support.WithUserDetails;
import pl.com.schoolsystem.base.BaseIntegrationTest;

@WithUserDetails("Admin@admin.pl")
public class BaseIntegrationTestAsAdmin extends BaseIntegrationTest {

  @BeforeEach
  void setUp() {
    super.createDropDatabase();
  }
}
