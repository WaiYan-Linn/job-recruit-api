package cs.job.recruit.api.input;

import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.validation.ValidSignUpForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidSignUpForm
public record SignUpForm(
		String companyName,
		String name,
		@NotBlank(message = "Please enter phone number.")
		String phone,
		@NotBlank(message = "Please enter email address.")
		String email,
		@NotBlank(message = "Please enter password.")
		String password,   
	    @NotNull(message = "Role is required.")
		Account.Role role           // Role field: Employer or JobSeeker
	     ) {

}
