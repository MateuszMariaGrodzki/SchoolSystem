package pl.com.schoolsystem.security.token;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class JwtClaim {
  public static final String AUTHORITY = "authority";
  public static final String FIRSTNAME = "firstname";

  public static final String LASTNAME = "lastname";
}
