package cs.job.recruit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import cs.job.recruit.api.input.UpdateEmployerRequest;
import cs.job.recruit.api.output.ApplicationResponseDto;
import cs.job.recruit.api.output.EmployerCandidateViewDto;
import cs.job.recruit.api.output.EmployerDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Employer;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.AccountRepository;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.EmployerRepo;
import cs.job.recruit.domain.repository.JobRepository;

@Service
public class EmployerService {

	@Value("${app.upload.dir}")
	private String uploadDir;

	@Autowired
	private EmployerRepo employerRepo;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;

    private static final Path RESUME_DIR = Paths.get("uploads/resumes");

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
	
	public List<ApplicationResponseDto> getApplicationsForJob(String employerEmail,Long jobId){
		
		Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
		
		if (!job.getEmployer().getAccount().getEmail().equalsIgnoreCase(employerEmail)) {
            throw new AccessDeniedException("Not authorized to view applicants for this job.");
        }

		// Fetch applications with JobSeeker and Account info
        List<Application> applications = applicationRepository.findByJobIdWithJobSeeker(jobId);
        return applications.stream()
        	    .map(app -> {
        	        JobSeeker js = app.getJobSeeker();
        	        return new ApplicationResponseDto(
        	        	app.getId(),
        	            js.getId(),
        	            js.getPersonalName(),
        	            js.getProfilePictureUrl(),
        	            js.getProfileSummary(),
        	            js.getSkills(),
        	            app.getAppliedAt(),
        	            app.getResumeUrl(),
        	            app.getStatus()
        	        );
        	    })
        	    .collect(Collectors.toList());

	}


	 public List<EmployerCandidateViewDto> getAllApplications(String employerEmail) {
	        Employer employer = employerRepo.findOneByAccountEmail(employerEmail)
	                .orElseThrow(() -> new RuntimeException("Employer not found"));

	        List<Application> applications = applicationRepository.findAllByEmployerId(employer.getId());

	        return applications.stream()
	                .map(app -> {
	                    Job job = app.getJob();
	                    JobSeeker js = app.getJobSeeker();
	                    return new EmployerCandidateViewDto(
	                        job.getId(),
	                        job.getTitle(),
	                        js.getId(),
	                        js.getPersonalName(),
	                        js.getProfilePictureUrl(),
	                        js.getProfileSummary(),
	                        js.getAccount().getEmail(),
	                        js.getAccount().getPhone(),
	                        js.getAccount().getAddress(),
	                        js.getDateOfBirth(),
	                        js.getSkills(),
	                        app.getId(),
	                        app.getAppliedAt(),
	                        app.getResumeUrl(),
	                        app.getStatus()
	                    );
	                })
	                .toList();
	    }


	 public ResponseEntity<Resource> downloadResumeForEmployer(Long applicationId, String employerEmail) throws IOException {
	        Employer employer = employerRepo.findOneByAccountEmail(employerEmail)
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employer not found"));

	        Application app = applicationRepository.findById(applicationId)
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

	        Job job = app.getJob();
	        if (!job.getEmployer().getId().equals(employer.getId())) {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
	        }

	        String filename = app.getResumeUrl();
	        if (!filename.endsWith(".pdf")) {
	            filename += ".pdf";
	        }

	        Path filePath = RESUME_DIR.resolve(filename).normalize();
	        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume file not found");
	        }

	        Resource resource = new UrlResource(filePath.toUri());
	        if (!resource.exists() || !resource.isReadable()) {
	            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File unreadable");
	        }

	        MediaType mediaType = resolveMediaType(filename);

	        return ResponseEntity.ok()
	                .contentType(mediaType)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
	                .body(resource);
	    }

	    private MediaType resolveMediaType(String filename) {
	        String ext = StringUtils.getFilenameExtension(filename);
	        if (ext != null) {
	            switch (ext.toLowerCase()) {
	                case "pdf": return MediaType.APPLICATION_PDF;
	                case "doc": return MediaType.parseMediaType("application/msword");
	                case "docx": return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	            }
	        }
	        return MediaType.APPLICATION_OCTET_STREAM;
	    }


		public void updateApplicationStatus(Long id, String status, String employerEmail) {
			// TODO Auto-generated method stub
			
		}


		public EmployerCandidateViewDto getApplicationDetail(Long id) {
			  
		      Application application = applicationRepository.findById(id).orElse(null);
		      
		      Job job = application.getJob();
              JobSeeker js = application.getJobSeeker();

		       
			return new EmployerCandidateViewDto(job.getId(), job.getTitle(), js.getId(), js.getPersonalName(), js.getProfilePictureUrl(), 
					js.getProfileSummary(), js.getAccount().getEmail(), js.getAccount().getPhone(), js.getAccount().getAddress(),
					js.getDateOfBirth(), js.getSkills(), application.getId(), application.getAppliedAt(), application.getResumeUrl(), application.getStatus());
		}
	
	}
