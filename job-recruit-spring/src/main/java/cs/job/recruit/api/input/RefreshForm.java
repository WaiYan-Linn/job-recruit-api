package cs.job.recruit.api.input;

import jakarta.validation.constraints.NotBlank;

public record RefreshForm(
		@NotBlank(message = "Please enter refresh token.")
		String refreshToken) {

}
