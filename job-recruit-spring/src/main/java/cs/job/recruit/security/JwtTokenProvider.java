package cs.job.recruit.security;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class JwtTokenProvider {

	public enum Type {
		Access, Refresh
	}



	@Value("${app.token.issuer}")
	private String issuer;
	@Value("${app.token.access}")
	private int access;
	@Value("${app.token.refresh}")
	private int refresh;

	private static final SecretKey KEY = Jwts.SIG.HS512.key().build();


	public String generate(Type type, Authentication authentication) {

		Date issuedAt = new Date();

		return Jwts.builder().subject(authentication.getName()).issuer(issuer).issuedAt(issuedAt)
				.expiration(getExpiration(type, issuedAt)).claim("type", type.name()).claim("rol", authentication
						.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.joining(",")))
				.signWith(KEY).compact();
	}
	

	public Authentication parse(Type type, String token) {
		try {
			var jwt = Jwts.parser().requireIssuer(issuer)
					.require("type", type.name())
					.verifyWith(KEY).build()
					.parseSignedClaims(token);
			var username = jwt.getPayload().getSubject();
			var role = jwt.getPayload().get("rol", String.class);
			var authorities = Arrays.stream(role.split(",")).map(a -> new SimpleGrantedAuthority(a)).toList();
			
			return UsernamePasswordAuthenticationToken.authenticated(username,null,authorities); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	private Date getExpiration(Type type, Date issuedAt) {
		var calendar = Calendar.getInstance();
		calendar.setTime(issuedAt);
		calendar.add(Calendar.MINUTE, type == Type.Access ? access : refresh);
		return calendar.getTime();
	}
}