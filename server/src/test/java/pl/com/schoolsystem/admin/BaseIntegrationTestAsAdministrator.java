package pl.com.schoolsystem.admin;

import org.springframework.security.test.context.support.WithUserDetails;
import pl.com.schoolsystem.base.BaseIntegrationTest;

@WithUserDetails("admin@admin.pl")
public class BaseIntegrationTestAsAdministrator extends BaseIntegrationTest {}
