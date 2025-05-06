package cs.job.recruit.api.input;

import java.time.LocalDate;

import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.Job.Category;
import cs.job.recruit.domain.entity.Job.Experience;
import cs.job.recruit.domain.entity.Job.JobType;
import cs.job.recruit.domain.entity.Job.WorkMode;

public record JobRequest(String title, Category category, String location, JobType jobType, WorkMode workMode,
		String experience, Double salaryMin, Double salaryMax, String description, String requirements, String benefits,
		LocalDate deadline, String applicationEmail) {
	
	public Job entity() {
	    var entity = new Job();
	    entity.setTitle(title);
	    entity.setCategory(category); // e.g., "IT"
	    entity.setLocation(location);
	    entity.setJobType(jobType);
	    entity.setWorkMode(workMode);
	    entity.setExperience(Experience.valueOf(experience)); // e.g., "Entry"
	    entity.setSalaryMin(salaryMin);
	    entity.setSalaryMax(salaryMax);
	    entity.setDescription(description);
	    entity.setRequirements(requirements);
	    entity.setBenefits(benefits);
	    entity.setDeadline(deadline);
	    entity.setApplicationEmail(applicationEmail);
	    entity.setPostedAt(LocalDate.now());
	    return entity;
	}

}
