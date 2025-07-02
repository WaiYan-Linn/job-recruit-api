package cs.job.recruit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.api.input.JobSeekerUpdateForm;
import cs.job.recruit.api.output.ApplicationSummary;
import cs.job.recruit.api.output.JobSeekerDetails;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.AccountRepository;
import cs.job.recruit.domain.repository.JobSeekerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class JobSeekerService {

	@Value("${app.upload.dir}")
	private String uploadDir;

	private final JobSeekerRepository jobSeekerRepository;
	private final AccountRepository accountRepository;

	public JobSeekerService(JobSeekerRepository jobSeekerRepository, AccountRepository accountRepository) {
		this.jobSeekerRepository = jobSeekerRepository;
		this.accountRepository = accountRepository;
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

}
