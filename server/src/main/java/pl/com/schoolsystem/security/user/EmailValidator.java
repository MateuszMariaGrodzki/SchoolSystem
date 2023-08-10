package pl.com.schoolsystem.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailValidator {

  private final ApplicationUserService applicationUserService;

  public boolean isEmailUniqueInDatabase(ApplicationUserEntity applicationUser, String email) {
    if (!isEmailFromRequestEqualToEmailFromDatabase(email, applicationUser.getEmail())) {
      return !applicationUserService.existsByEmailIgnoreCase(email);
    }
    return true;
  }

  private boolean isEmailFromRequestEqualToEmailFromDatabase(
      String requestEmail, String databaseEmail) {
    return requestEmail.equals(databaseEmail);
  }
}
