package cs.job.recruit.api.output;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import cs.job.recruit.domain.entity.Application;

public record EmployerCandidateViewDto(
    Long jobId,
    String jobTitle,

    UUID jobSeekerId,
    String jobSeekerName,
    String profilePictureUrl,
    String profileSummary,
    String email, 
    String phoneNumber, 
    String address, 
    LocalDate dateOfBirth,
    List<String> skills,

    Long applicationId,
    LocalDateTime appliedAt,
    String resumeUrl,
    Application.Status status
    
) {}
