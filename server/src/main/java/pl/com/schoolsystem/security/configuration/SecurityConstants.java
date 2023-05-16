package pl.com.schoolsystem.security.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {

  public static final String TOKEN_PREFIX = "Bearer ";

  public static final String TOKEN_ENDPOINT = "/token";
}
