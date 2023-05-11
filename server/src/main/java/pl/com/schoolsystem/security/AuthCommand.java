package pl.com.schoolsystem.security;

import lombok.Value;

@Value
public class AuthCommand {
  String username;
  String password;
}
