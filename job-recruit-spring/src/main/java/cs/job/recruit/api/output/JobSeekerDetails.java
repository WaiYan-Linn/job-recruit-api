package cs.job.recruit.api.output;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;

public record JobSeekerDetails(UUID id, String personalName, String profileSummary, String profilePictureUrl,
		String email, String phoneNumber, String address, LocalDate dateOfBirth, List<String> skills,
		List<ApplicationSummary> applications // << useful summary
) {
}
