package cs.job.recruit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.api.input.UpdateEmployerRequest;
import cs.job.recruit.api.output.EmployerDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Employer;
import cs.job.recruit.domain.repository.AccountRepository;
import cs.job.recruit.domain.repository.EmployerRepo;

@Service
public class EmployerService {

	@Value("${app.upload.dir}")
	private String uploadDir;

	@Autowired
	private EmployerRepo employerRepo;
	
	@Autowired
	private AccountRepository accountRepository;


	public String uploadProfilePicture(String employerEmail, MultipartFile file) {

		Employer employer = employerRepo.findOneByAccountEmail(employerEmail)
				.orElseThrow(() -> new RuntimeException("Employer not found"));

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(uploadDir, fileName);
		try {
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, file.getBytes());

			String imageUrl = "/pictures/" + fileName;
			employer.setProfilePictureUrl(imageUrl);
			employerRepo.save(employer);

			return "http://localhost:8080" + imageUrl;

		} catch (IOException e) {
			throw new RuntimeException("Failed to store file", e);
		}
	}


	public EmployerDetails updateCurrentEmployer(String email, UpdateEmployerRequest req) {
	    Employer emp = employerRepo.findOneByAccountEmail(email)
		        .orElseThrow(() -> new RuntimeException("Employer not found"));
	    Account account = emp.getAccount();

	    // Update Employer fields
	    if (req.companyName() != null) {
	        emp.setCompanyName(req.companyName());
	    }
	    if (req.website() != null) {
	        emp.setWebsite(req.website());
	    }
	    if (req.aboutUs() != null) {
	        emp.setAboutUs(req.aboutUs());
	    }

	    // Update Account fields
	    if (req.phoneNumber() != null) {
	        account.setPhone(req.phoneNumber());
	    }
	    if (req.address() != null) {
	        account.setAddress(req.address());
	    }
	    

	    Employer updated = employerRepo.save(emp);
	    accountRepository.save(account);
	    

	    return new EmployerDetails(
	        updated.getId(),
	        updated.getCompanyName(),
	        updated.getWebsite(),
	        updated.getProfilePictureUrl(),
	        updated.getAboutUs(),
	        account.getAddress(),
	        account.getPhone()
	    );
	}


	public EmployerDetails getCurrentEmployerProfile(String email) {
		    Employer employer = employerRepo.findOneByAccountEmail(email)
		        .orElseThrow(() -> new RuntimeException("Employer not found"));

		    Account account = employer.getAccount();

		    return new EmployerDetails(
		        employer.getId(),
		        employer.getCompanyName(),
		        employer.getWebsite(),
		        employer.getProfilePictureUrl(),
		        employer.getAboutUs(),
		        account.getAddress(),
		        account.getPhone()
		    );
		}


	public PageResult<EmployerDetails> getEmployerDetailsByName(String name, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<EmployerDetails> resultPage;

	    if (name == null || name.trim().isEmpty()) {
	        resultPage = employerRepo.findAllEmployerDetails(pageable);
	    } else {
	        resultPage = employerRepo.findByCompanyNameContainingIgnoreCase(name, pageable);
	    }
	    return new PageResult<>(
	            resultPage.getContent(),
	            resultPage.getTotalElements(),
	            resultPage.getSize(),
	            resultPage.getNumber()
	    );
	}


	public EmployerDetails getCurrentEmployerProfile(UUID id) {
		Employer employer = employerRepo.findById(id).orElseThrow(() -> new RuntimeException("Employer not found"));
		 Account account = employer.getAccount();

		    return new EmployerDetails(
		        employer.getId(),
		        employer.getCompanyName(),
		        employer.getWebsite(),
		        employer.getProfilePictureUrl(),
		        employer.getAboutUs(),
		        account.getAddress(),
		        account.getPhone()
		    );
		}
	}





