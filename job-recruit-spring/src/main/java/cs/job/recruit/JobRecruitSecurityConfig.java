package cs.job.recruit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import cs.job.recruit.security.JwtTokenFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity  // For Spring Security 6+
@EnableWebSecurity
@RequiredArgsConstructor
@PropertySource(value ="classpath:/token.properties")
public class JobRecruitSecurityConfig {
	
	private final JwtTokenFilter jwtTokenFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http.csrf(csrf -> csrf.disable());
		http.cors(cors -> {});
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.authorizeHttpRequests(request -> {
			request.requestMatchers("/security/**").permitAll();
			request.requestMatchers("/employer/**", "/jobs/**").hasRole("EMPLOYER"); // ✅ FIXED
			request.anyRequest().authenticated();
		});
		
		http.addFilterAfter(jwtTokenFilter, ExceptionTranslationFilter.class);
		
           

        return http.build();
    }
    
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
    
    
}