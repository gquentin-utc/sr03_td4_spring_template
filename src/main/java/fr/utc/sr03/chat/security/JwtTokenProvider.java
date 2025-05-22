package fr.utc.sr03.chat.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtTokenProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${api.security.token.signatureSecretKey}")
	private String secretKey;
	
	@Value("${api.security.token.validityInMilliseconds}")
	private long validityInMilliseconds;
	
	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createSimpleToken(String login, String role) {
		// Dates de creation et d'expiration
		Date now = new Date();
		Date expiration = new Date(now.getTime() + validityInMilliseconds);

		// SecretKey
		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

		return Jwts.builder()
				.subject((login))
				.claim("role", role != null ? role : "")
				.issuedAt(now)
				.expiration(expiration)
				.signWith(key)
				.compact();
	}

	public Optional<Authentication> getAuthentication(String token) {
		if (isTokenValid(token)) {
			// Recup payload
			DefaultClaims claims = (DefaultClaims) Jwts.parser()
					.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
					.build()
					.parse(token)
					.getPayload();

			// Recup username
			String username = claims.getSubject();

			// Recup authorities (role)
			List<? extends GrantedAuthority> authorities = new ArrayList<>();
			if (claims.get("role") != null) {
				authorities = Stream.of(claims.get("role").toString())
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
			}

			// Spring AuthenticationToken
			return Optional.of(new UsernamePasswordAuthenticationToken(username, "", authorities));
		}

		return Optional.empty();
	}
	
	private boolean isTokenValid(String token){
		if (token != null && !token.isEmpty()) {
			try {
				Jwts.parser()
						.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
						.build()
						.parse(token);
				return true;
			} catch (ExpiredJwtException e) {
				LOGGER.error("Le token a expire");
			} catch (Exception e) {
				LOGGER.error("Erreur lors de la validation du token : " + e.getMessage());
			}
		}

		return false;
	}
}