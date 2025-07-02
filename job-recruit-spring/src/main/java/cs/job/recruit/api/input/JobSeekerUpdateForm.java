package cs.job.recruit.api.input;

import java.time.LocalDate;
import java.util.List;

public record JobSeekerUpdateForm(
	    String personalName,
	    String profileSummary,
	    String profilePictureUrl,
	    LocalDate dateOfBirth,
	    List<String> skills,
	    String phoneNumber,
	    String address
	) {}
