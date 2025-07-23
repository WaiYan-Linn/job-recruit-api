package cs.job.recruit.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cs.job.recruit.api.input.SignUpForm;
import cs.job.recruit.api.output.SignUpResult;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Employer;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSignUpService {
	
	private final AccountRepository accountRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public SignUpResult signUp(SignUpForm form) {
		
		if(accountRepo.findOneByEmail(form.email()).isPresent()) {
			throw new IllegalArgumentException("Your email has already been used.");
		}
		  Account account = new Account();
		  	account.setPhone(form.phone());
		  	account.setActivated(true);
		    account.setEmail(form.email());
		    account.setPassword(passwordEncoder.encode(form.password()));
		    account.setRole(form.role());

		    if (form.role() == Account.Role.EMPLOYER) {
		        Employer employer = new Employer();
		        employer.setCompanyName(form.companyName());
		        employer.setAccount(account);
		        // Set other employer-specific fields here
		        account.setEmployer(employer);
		    } else {
		        JobSeeker jobSeeker = new JobSeeker();
		        jobSeeker.setPersonalName(form.name());
		        jobSeeker.setAccount(account);
		        account.setJobSeeker(jobSeeker);

		        // Set job seeker–specific fields if any
		    }
		    
		    accountRepo.saveAndFlush(account);
		
		return SignUpResult.from(account);
		    
	}
	
}
