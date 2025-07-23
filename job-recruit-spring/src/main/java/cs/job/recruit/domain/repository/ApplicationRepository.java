package cs.job.recruit.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Application.Status;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	boolean existsByJobSeekerAndJob(JobSeeker jobSeeker, Job job);

	List<Application> findByJobSeekerAndJobIdIn(JobSeeker seeker, List<Long> jobIds);

	@Query("SELECT a FROM Application a JOIN FETCH a.jobSeeker js JOIN FETCH js.account acc WHERE a.job.id = :jobId")
	List<Application> findByJobIdWithJobSeeker(@Param("jobId") Long jobId);

	@Query("""
		    SELECT a FROM Application a
		    JOIN FETCH a.job j
		    JOIN FETCH j.employer e
		    JOIN FETCH a.jobSeeker js
		    JOIN FETCH js.account acc
		    WHERE e.id = :employerId
		""")
		List<Application> findAllByEmployerId(@Param("employerId") UUID employerId);

	@Query("""
		    SELECT a FROM Application a
		    JOIN FETCH a.job j
		    JOIN FETCH j.employer e
		    JOIN FETCH a.jobSeeker js
		    JOIN FETCH js.account acc
		    WHERE e.id = :employerId
		""")
	Application findOneByEmployerId(@Param("employerId") UUID employerId);

	List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);

	List<Application> findByStatusOrderByAppliedAtDesc(Status status);
	
	List<Application> findByJobSeekerId(UUID id);
	
	@Query("""
			 SELECT a FROM Application a
		    JOIN FETCH a.job j
		    JOIN FETCH j.employer e
		    JOIN FETCH a.jobSeeker js
			LEFT JOIN FETCH a.interview i
		    WHERE js.id = :jobSeekerId
			""")
	List<Application> findAllByJobSeekerId(UUID jobSeekerId);
	
	@Query("""
				SELECT a FROM Application a
			    JOIN FETCH a.job j
			    JOIN FETCH j.employer e
			    JOIN FETCH a.jobSeeker js
			    LEFT JOIN FETCH a.interview i
			    WHERE js.id = :jobSeekerId
			""")
	Application findOneByJobSeekerId(UUID jobSeekerId);



}
