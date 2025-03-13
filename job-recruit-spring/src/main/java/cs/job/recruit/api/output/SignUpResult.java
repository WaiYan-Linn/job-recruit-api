package cs.job.recruit.api.output;

import java.util.UUID;

import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Account.Role;

public record SignUpResult(
		UUID id,
		String name,
		String email,
		Role role) {

	public static SignUpResult from(Account entity) {
		return new SignUpResult(entity.getId(),entity.getDisplayName(), entity.getEmail(), entity.getRole());
	}
}
