package cs.job.recruit.api.output;

import java.time.LocalDateTime;

import cs.job.recruit.domain.entity.Application.Status;

public record ApplicationInfo(Long applicationId,
		String JobTitle,
		String jobDescription,
		LocalDateTime appliedAt,
		String companyName, String profilePictureUrl,
		String email,
		String dateTime,
		String location, String notes, Status status) {

}
