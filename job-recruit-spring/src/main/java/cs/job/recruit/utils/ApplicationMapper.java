package cs.job.recruit.utils;

import org.springframework.stereotype.Component;

import cs.job.recruit.api.output.EmployerCandidateViewDto;
import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;

@Component
public class ApplicationMapper {

    public EmployerCandidateViewDto EmployerCandidateViewDto(Application application) {
        Job job = application.getJob();
        JobSeeker js = application.getJobSeeker();

        return new EmployerCandidateViewDto(
                job.getId(),
                job.getTitle(),
                js.getId(),
                js.getPersonalName(),
                js.getProfilePictureUrl(),
                js.getProfileSummary(),
                js.getAccount().getEmail(),
                js.getAccount().getPhone(),
                js.getAccount().getAddress(),
                js.getDateOfBirth(),
                js.getSkills(),
                application.getId(),
                application.getAppliedAt(),
                application.getResumeUrl(),
                application.getStatus()
        );
    }
}
