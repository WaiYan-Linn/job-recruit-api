package cs.job.recruit.api.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import cs.job.recruit.domain.entity.Application.Status;

public record ApplicationResponseDto(
	Long id,
    UUID jobSeekerId,
    String jobSeekerName,
    String profilePictureUrl,
    String profileSummary,
    List<String> skills,
    LocalDateTime appliedAt,
    String resumeUrl,
   Status status
) {}
