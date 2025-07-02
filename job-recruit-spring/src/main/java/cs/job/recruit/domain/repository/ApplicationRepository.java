package cs.job.recruit.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
  
	boolean existsByJobSeekerAndJob(JobSeeker jobSeeker, Job job);

	List<Application> findByJobSeekerId(UUID id);

	List<Application> findByJobSeekerAndJobIdIn(JobSeeker seeker, List<Long> jobIds);
}
