package pl.com.schoolsystem.security.token;

import static java.time.temporal.ChronoUnit.HOURS;
import static pl.com.schoolsystem.security.token.JwtClaim.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.com.schoolsystem.security.user.ApplicationUserEntity;

@Service
public class JWTService {

  private final String SECRET_KEY;

  private static final long HOURS_OF_TOKEN_VALIDITY = 8;

  public JWTService(@Value("${jwt.secret.key}") String secretKey) {
    SECRET_KEY = secretKey;
  }

  public String extractUserEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userName = extractUserEmail(token);
    return (userName.equals(userDetails.getUsername()) && !isTokeExpired(token));
  }

  private boolean isTokeExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public String generateToken(UserDetails userDetails) {
    final var additionalClaims = getAdditionalClaims(userDetails);
    return generateToken(additionalClaims, userDetails, Instant.now());
  }

  public String generateToken(
      Map<String, Object> additionalClaims, UserDetails userDetails, Instant issuedAt) {
    return Jwts.builder()
        .setClaims(additionalClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(Date.from(issuedAt))
        .setExpiration(Date.from(issuedAt.plus(HOURS_OF_TOKEN_VALIDITY, HOURS)))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Map<String, Object> getAdditionalClaims(UserDetails userDetails) {
    final var applicationUserEntity = (ApplicationUserEntity) userDetails;
    final var claims = new HashMap<String, Object>();
    claims.put(AUTHORITY, applicationUserEntity.getRole());
    claims.put(FIRSTNAME, applicationUserEntity.getFirstName());
    claims.put(LASTNAME, applicationUserEntity.getLastName());
    return claims;
  }
}
