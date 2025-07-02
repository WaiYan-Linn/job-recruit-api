package cs.job.recruit.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.JobRepository;
import cs.job.recruit.domain.repository.JobSeekerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final JobRepository jobRepository;
	private final JobSeekerRepository jobSeekerRepository;
	private final ResumeService resumeService;

	@Transactional
	public void applyWithResume(String email, Long jobId, MultipartFile file) {

		JobSeeker seeker = jobSeekerRepository.findOneByAccountEmail(email)
				.orElseThrow(() -> new IllegalStateException("JobSeeker not found"));

		Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found"));

		// Prevent duplicate applications
		boolean exists = applicationRepository.existsByJobSeekerAndJob(seeker, job);
		if (exists) {
			throw new IllegalStateException("Already applied to this job");
		}

		// Store resume and update seeker.resumeUrl
		String filename;
		try {
			filename = resumeService.storeResume(email, file);
		} catch (IOException e) {
			throw new RuntimeException("Failed to store resume", e);
		}

		// Create application
		Application app = new Application();
		app.setJobSeeker(seeker);
		app.setJob(job);
		app.setResumeUrl(filename);
		applicationRepository.save(app);

		// Optionally, record a status history entry here
	}
	
	public boolean hasApplied(String email, long jobId) {
	    JobSeeker seeker = jobSeekerRepository
	            .findOneByAccountEmail(email)
	            .orElseThrow(() -> new IllegalStateException("JobSeeker not found"));
	    Job job = jobRepository
	            .findById(jobId)
	            .orElseThrow(() -> new IllegalArgumentException("Job not found"));
	    return applicationRepository.existsByJobSeekerAndJob(seeker, job);
	}


}
