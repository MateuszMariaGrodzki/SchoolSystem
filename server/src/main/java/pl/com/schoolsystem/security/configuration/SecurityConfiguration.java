package pl.com.schoolsystem.security.configuration;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.com.schoolsystem.security.authentication.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable();

    configureEndpoints(http);
    disableSessionCreation(http);
    addJwtFilter(http);
    addAuthenticationProvider(http);

    return http.build();
  }

  private void configureEndpoints(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .requestMatchers(POST, "/v1/token")
        .permitAll()
        .anyRequest()
        .authenticated();
  }

  private void disableSessionCreation(HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(STATELESS);
  }

  private void addJwtFilter(HttpSecurity http) {
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }

  private void addAuthenticationProvider(HttpSecurity http) {
    http.authenticationProvider(authenticationProvider);
  }
}
