package pl.com.schoolsystem.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.com.schoolsystem.security.user.ApplicationUserService;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final ApplicationUserService applicationUserService;

  private final PasswordConfig passwordConfig;

  @Bean
  public UserDetailsService userDetailsService() {
    return applicationUserService::getByEmailsOrElseThrowApplicationUserNotFoundException;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordConfig.passwordEncoder());
    provider.setUserDetailsService(userDetailsService());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
