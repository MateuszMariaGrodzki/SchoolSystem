package pl.com.schoolsystem.teacher;

import org.springframework.security.test.context.support.WithUserDetails;
import pl.com.schoolsystem.base.BaseIntegrationTest;

@WithUserDetails("teacher@gruszka.pl")
public class BaseIntegrationTestAsTeacher extends BaseIntegrationTest {}
