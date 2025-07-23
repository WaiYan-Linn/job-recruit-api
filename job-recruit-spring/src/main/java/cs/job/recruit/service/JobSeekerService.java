package cs.job.recruit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.api.input.JobSeekerUpdateForm;
import cs.job.recruit.api.output.ApplicationInfo;
import cs.job.recruit.api.output.ApplicationSummary;
import cs.job.recruit.api.output.JobSeekerDetails;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Employer;
import cs.job.recruit.domain.entity.Interview;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.AccountRepository;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.JobSeekerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class JobSeekerService {

	@Value("${app.upload.dir}")
	private String uploadDir;

	private final JobSeekerRepository jobSeekerRepository;
	private final AccountRepository accountRepository;
	private final ApplicationRepository applicationRepository;

	public JobSeekerService(JobSeekerRepository jobSeekerRepository, AccountRepository accountRepository,ApplicationRepository applicationRepository) {
		this.jobSeekerRepository = jobSeekerRepository;
		this.accountRepository = accountRepository;
		this.applicationRepository = applicationRepository;
	}

	public JobSeekerDetails getJobSeekerDetails(String email) {
		Account account = accountRepository.findOneByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Account not found for email: " + email));

		JobSeeker jobSeeker = jobSeekerRepository.findByAccountId(account.getId())
				.orElseThrow(() -> new EntityNotFoundException("JobSeeker not found"));

		return new JobSeekerDetails(jobSeeker.getId(), jobSeeker.getPersonalName(), jobSeeker.getProfileSummary(),
				jobSeeker.getProfilePictureUrl(), account.getEmail(), account.getPhone(), account.getAddress(),
				jobSeeker.getDateOfBirth(), jobSeeker.getSkills(),
				jobSeeker.getApplications().stream()
						.map(app -> new ApplicationSummary(app.getId(), app.getJob().getTitle(), app.getAppliedAt(),
								app.getJob().getEmployer().getCompanyName(), app.getStatus().name()))
						.toList());
	}

	@Transactional
	public JobSeekerDetails updateJobSeekerDetails(String email, JobSeekerUpdateForm dto) {
		Account account = accountRepository.findOneByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Account not found"));

		JobSeeker jobSeeker = jobSeekerRepository.findById(account.getId())
				.orElseThrow(() -> new EntityNotFoundException("JobSeeker not found"));

		// Update Account info
		account.setPhone(dto.phoneNumber());
		account.setAddress(dto.address());

		// Update JobSeeker info
		jobSeeker.setPersonalName(dto.personalName());
		jobSeeker.setProfileSummary(dto.profileSummary());
		jobSeeker.setDateOfBirth(dto.dateOfBirth());
		jobSeeker.setSkills(dto.skills());

		// Save both (JPA cascade takes care of it in one shot if needed)
		accountRepository.save(account);
		jobSeekerRepository.save(jobSeeker);

		return getJobSeekerDetails(email);
	}

	public String uploadProfilePicture(String email, MultipartFile file) {
		JobSeeker jobSeeker = jobSeekerRepository.findOneByAccountEmail(email)
				.orElseThrow(() -> new RuntimeException("Employer not found"));

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(uploadDir, fileName);
		try {
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, file.getBytes());

			String imageUrl = "/pictures/" + fileName;
			jobSeeker.setProfilePictureUrl(imageUrl);
			jobSeekerRepository.save(jobSeeker);

			return "http://localhost:8080" + imageUrl;

		} catch (IOException e) {
			throw new RuntimeException("Failed to store file", e);
		}
	}

	public List<ApplicationInfo> getAllApplications(String jobSeekerEmail) {
	    JobSeeker jobSeeker = jobSeekerRepository.findOneByAccountEmail(jobSeekerEmail)
	            .orElseThrow(() -> new RuntimeException("JobSeeker not found"));
	    
	    List<Application> applications = applicationRepository.findAllByJobSeekerId(jobSeeker.getId());

	    return applications.stream().map(app -> {
	        Job job = app.getJob();
	        Employer employer = job.getEmployer();
	        Interview interview = app.getInterview();

	        String interviewDateTime = interview != null && interview.getInterviewDateTime() != null
	                ? interview.getInterviewDateTime().toString()
	                : "";

	        String location = interview != null && interview.getLocation() != null
	                ? interview.getLocation()
	                : "";

	        String notes = interview != null && interview.getNotes() != null
	                ? interview.getNotes()
	                : "";

	        return new ApplicationInfo(
	                app.getId(),
	                job.getTitle(),
		            job.getDescription(),
	                app.getAppliedAt(),
	                employer.getCompanyName(),
	                employer.getProfilePictureUrl(),
	                employer.getAccount().getEmail(),
	                interviewDateTime,
	                location,
	                notes,
	                app.getStatus()
	        );
	    }).toList();
	}


	public ApplicationInfo getApplicationsDetails(String jobSeekerEmail, Long applicationId) {
	    JobSeeker jobSeeker = jobSeekerRepository.findOneByAccountEmail(jobSeekerEmail)
	            .orElseThrow(() -> new RuntimeException("JobSeeker not found"));

	    Application application = applicationRepository.findOneByJobSeekerId(jobSeeker.getId());

	    Job job = application.getJob();
	    Employer employer = job.getEmployer();
	    Interview interview = application.getInterview();

	    String interviewDateTime = interview != null && interview.getInterviewDateTime() != null
	            ? interview.getInterviewDateTime().toString()
	            : "";

	    String location = interview != null && interview.getLocation() != null
	            ? interview.getLocation()
	            : "";

	    String notes = interview != null && interview.getNotes() != null
	            ? interview.getNotes()
	            : "";

	    return new ApplicationInfo(
	            applicationId,
	            job.getTitle(),
	            job.getDescription(),
	            application.getAppliedAt(),
	            employer.getCompanyName(),
	            employer.getProfilePictureUrl(),
	            employer.getAccount().getEmail(),
	            interviewDateTime,
	            location,
	            notes,
	            application.getStatus()
	    );
	}

}
