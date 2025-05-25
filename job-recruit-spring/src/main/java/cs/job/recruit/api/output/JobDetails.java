package cs.job.recruit.api.output;

import java.time.LocalDate;

import cs.job.recruit.domain.entity.Employer;
import cs.job.recruit.domain.entity.Employer_;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.Job.Category;
import cs.job.recruit.domain.entity.Job.Experience;
import cs.job.recruit.domain.entity.Job.JobType;
import cs.job.recruit.domain.entity.Job.WorkMode;
import cs.job.recruit.domain.entity.Job_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

public record JobDetails(Long id,String title, Category category, String location, JobType jobType, WorkMode workMode,
		Experience experience, Double salaryMin, Double salaryMax, String description, String requirements,
		String benefits, LocalDate deadline, String applicationEmail, LocalDate postedAt, EmployerBasic employer) {

	public static void select(CriteriaBuilder cb, CriteriaQuery<JobDetails> cq, Root<Job> root) {
		
		Join<Job, Employer> employerJoin = root.join(Job_.employer);
        
        cq.multiselect(
        	root.get(Job_.id),
            root.get(Job_.title),         // String
            root.get(Job_.category),      // Category enum
            root.get(Job_.location),      // String
            root.get(Job_.jobType),       // JobType enum
            root.get(Job_.workMode),      // WorkMode enum
            root.get(Job_.experience),    // Experience enum
            root.get(Job_.salaryMin),     // Double
            root.get(Job_.salaryMax),     // Double
            root.get(Job_.description),   // String
            root.get(Job_.requirements),  // String
            root.get(Job_.benefits),      // String
            root.get(Job_.deadline),      // LocalDate
            root.get(Job_.applicationEmail),// String
            root.get(Job_.postedAt),      // LocalDate
            cb.construct(EmployerBasic.class,
                    employerJoin.get(Employer_.id),
                    employerJoin.get(Employer_.companyName),
                    employerJoin.get(Employer_.website),
                    employerJoin.get(Employer_.profilePictureUrl)
                ));            // Employer entity
	}

}
