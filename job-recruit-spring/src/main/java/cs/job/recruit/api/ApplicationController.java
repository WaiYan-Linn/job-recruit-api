package cs.job.recruit.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.service.ApplicationService;

@RestController
@RequestMapping("/app")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@PostMapping(value = "/apply/{jobId}", consumes = "multipart/form-data")
	public ResponseEntity<String> applyToJob(Authentication authentication, @PathVariable Long jobId,
			@RequestParam("resumeFile") MultipartFile resumeFile) {
		String email = authentication.getName();

		// principal.getName() should be the Account ID (UUID string)
		applicationService.applyWithResume(email, jobId, resumeFile);
		return ResponseEntity.ok("Application submitted successfully");
	}
	
	@GetMapping("/{id}/has-applied")
	public boolean hasApplied(@PathVariable long id, Authentication authentication) {
	    String email = authentication.getName();
	    return applicationService.hasApplied(email, id);
	}


}
