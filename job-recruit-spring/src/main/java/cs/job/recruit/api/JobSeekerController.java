package cs.job.recruit.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.api.input.JobSeekerUpdateForm;
import cs.job.recruit.api.output.JobSeekerDetails;
import cs.job.recruit.service.JobSeekerService;

@RestController
@RequestMapping("jobseeker")
public class JobSeekerController {

	@Autowired
	private JobSeekerService jobSeekerService;

	@GetMapping
	public ResponseEntity<?> getJobSeeker(Authentication authentication) {
	    if (authentication == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    }

	    String email = authentication.getName();
	    JobSeekerDetails details = jobSeekerService.getJobSeekerDetails(email);
	    return ResponseEntity.ok(details);
	}

    @PutMapping("/profile/update")
    public JobSeekerDetails updateProfile(
            Authentication authentication,
            @RequestBody JobSeekerUpdateForm form
    ) {
        return jobSeekerService.updateJobSeekerDetails(authentication.getName(), form);
    }
    

    @PostMapping("/profile/picture")
    public String uploadPicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        String email = authentication.getName();
        return jobSeekerService.uploadProfilePicture(email, file);
    }
    
}
