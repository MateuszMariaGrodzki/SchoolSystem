package pl.com.schoolsystem.admin;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import pl.com.schoolsystem.base.BaseIntegrationTest;

@WithUserDetails("Admin@admin.pl")
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:create_administrator.sql")
public class BaseIntegrationTestAsAdministrator extends BaseIntegrationTest {}
