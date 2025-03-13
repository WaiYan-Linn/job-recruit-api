package cs.job.recruit.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import cs.job.recruit.security.JwtTokenProvider.Type;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider tokenProvider;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var accessToken = request.getHeader("Authorization");
		if(!StringUtils.hasLength(accessToken)) {
			var authentication = tokenProvider.parse(Type.Access, accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

		}
		
		filterChain.doFilter(request, response);

	}

}
