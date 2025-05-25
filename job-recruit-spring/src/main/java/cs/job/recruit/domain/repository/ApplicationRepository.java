package cs.job.recruit.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJob_Employer_Id(UUID employerId);
    List<Application> findByJob_Id(Long jobId);
    List<Application> findByJobSeekerId(UUID jobSeekerId);
    Optional<Application> findByJob_IdAndJobSeeker_Id(Long jobId, UUID jobSeekerId);
	boolean existsByJobSeekerAndJob(JobSeeker jobSeeker, Job job);
}
