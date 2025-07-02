package cs.job.recruit.api.output;

import java.time.LocalDateTime;

public record ApplicationSummary(
	    Long id,
	    String jobTitle,
	    LocalDateTime appliedDate,
	    String companyName,
	    String status
	) {}
