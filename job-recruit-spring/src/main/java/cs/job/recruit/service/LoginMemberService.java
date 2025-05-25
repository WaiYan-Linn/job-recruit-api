package cs.job.recruit.service;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cs.job.recruit.domain.entity.Employer;
import cs.job.recruit.domain.repository.EmployerRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginMemberService {
	
	private final EmployerRepo employerRepo;
	
	@Transactional(readOnly = true)
	public Employer getLoginUser() {
		var username = SecurityContextHolder.getContext().getAuthentication().getName();
		return employerRepo.findOneByAccountEmail(username).get();
	}

}
