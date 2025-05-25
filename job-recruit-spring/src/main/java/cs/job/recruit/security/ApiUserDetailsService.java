package cs.job.recruit.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cs.job.recruit.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiUserDetailsService implements UserDetailsService{
	
	private final AccountRepository accountRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return accountRepo.findOneByEmail(username)
				.map(account -> User.builder()
						.username(username)
						.password(account.getPassword())
						.authorities("ROLE_" + account.getRole().name())
						.build())
				.orElseThrow(() -> new UsernameNotFoundException("Please check your login id."));
	}
	
}
