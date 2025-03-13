package cs.job.recruit.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Account.Role;
import cs.job.recruit.respository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AdminUserInitializer {

	private final PasswordEncoder passwordEncoder;
	private final AccountRepository accountRepo;
	
	@Transactional
	@EventListener(classes = ContextRefreshedEvent.class)
	public void initialize() {
		
		if(accountRepo.count() == 0) {
			
			var admin = new Account();
			admin.setEmail("admin@gmail.com");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setRole(Role.ADMIN);
			
			accountRepo.save(admin);
		}
	}
}
