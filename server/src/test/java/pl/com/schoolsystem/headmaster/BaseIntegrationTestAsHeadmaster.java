package pl.com.schoolsystem.headmaster;

import org.springframework.security.test.context.support.WithUserDetails;
import pl.com.schoolsystem.base.BaseIntegrationTest;

@WithUserDetails("head@master.pl")
public class BaseIntegrationTestAsHeadmaster extends BaseIntegrationTest {}
