package cs.job.recruit.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cs.job.recruit.api.input.RefreshForm;
import cs.job.recruit.api.input.SignInForm;
import cs.job.recruit.api.output.AccountInfo;
import cs.job.recruit.domain.repository.AccountRepository;
import cs.job.recruit.security.JwtTokenProvider;
import cs.job.recruit.security.JwtTokenProvider.Type;
import static cs.job.recruit.utils.EntityOperationUtils.safeCall;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenManagementService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final AccountRepository accountRepository;
	
	@Transactional(readOnly = true)
	public AccountInfo generate(SignInForm form) {
		
		// Authenticate
		var authentication = authenticationManager.authenticate(form.authToken());
		
		// Get Account Info
		var account = safeCall(accountRepository.findOneByEmail(form.email()), "Account", form.email());
		
		// Generate Token from Authentication Object
		return AccountInfo.builder()
				.email(form.email())
				.name(account.getDisplayName())
				.role(account.getRole())
				.accessToken(tokenProvider.generate(Type.Access, authentication))
				.refreshToken(tokenProvider.generate(Type.Refresh, authentication))
				.build();
	}

	public AccountInfo refresh(RefreshForm form) {
		
		// Authenticate
		var authentication = tokenProvider.parse(Type.Refresh, form.refreshToken());
		
		// Get Account Info
		var account = safeCall(accountRepository.findOneByEmail(authentication.getName()), "Account", authentication.getName());
		
		// Generate Token from Authentication Object
		return AccountInfo.builder()
				.email(authentication.getName())
				.name(account.getDisplayName())
				.role(account.getRole())
				.accessToken(tokenProvider.generate(Type.Access, authentication))
				.refreshToken(tokenProvider.generate(Type.Refresh, authentication))
				.build();
	}

}
