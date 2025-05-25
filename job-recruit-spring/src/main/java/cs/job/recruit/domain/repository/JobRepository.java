package cs.job.recruit.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.domain.BaseRepository;
import cs.job.recruit.domain.entity.Job;

public interface JobRepository extends BaseRepository<Job, Long> {

	@Query("""
		    SELECT new cs.job.recruit.api.output.JobDetails(
		        j.id, j.title, j.category, j.location, j.jobType, j.workMode, j.experience,
		        j.salaryMin, j.salaryMax, j.description, j.requirements, j.benefits,
		        j.deadline, j.applicationEmail, j.postedAt,
		        new cs.job.recruit.api.output.EmployerBasic(e.id, e.companyName, e.website, e.profilePictureUrl)
		    )
		    FROM Job j
		    JOIN j.employer e
		    WHERE j.id = :id
		""")
		Optional<JobDetails> findJobDetailsById(@Param("id") Long id);
	



}
