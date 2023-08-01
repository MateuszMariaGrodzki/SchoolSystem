package pl.com.schoolsystem.student;

import org.springframework.security.test.context.support.WithUserDetails;
import pl.com.schoolsystem.base.BaseIntegrationTest;

@WithUserDetails("trzezwy@student.pl")
public class BaseIntegrationTestAsStudent extends BaseIntegrationTest {}
