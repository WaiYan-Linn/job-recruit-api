package cs.job.recruit.api;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.api.input.InterviewRequest;
import cs.job.recruit.api.input.UpdateEmployerRequest;
import cs.job.recruit.api.output.ApplicationResponseDto;
import cs.job.recruit.api.output.EmployerCandidateViewDto;
import cs.job.recruit.api.output.EmployerDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.service.EmployerService;
import cs.job.recruit.service.InterviewService;

@RestController
@RequestMapping("employer")
public class EmployerController {

    @Autowired
    private EmployerService employerService;
    
    @Autowired
    private InterviewService interviewService;
    
    @GetMapping("/all")
    public PageResult<EmployerDetails> getEmployerDetails(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        var result= employerService.getEmployerDetailsByName(name, page, size);
        return result;
    }
    @GetMapping("/profile/about")
    public EmployerDetails getProfile(Authentication authentication) {
        String email = authentication.getName();
        return employerService.getCurrentEmployerProfile(email);

    }
    
    @GetMapping("{id}")
    public EmployerDetails getEmployerById(@PathVariable UUID id) {
    	return employerService.getCurrentEmployerProfile(id);
    }

    @PutMapping("/profile/update")
    public EmployerDetails updateProfile(
            Authentication authentication,
            @RequestBody UpdateEmployerRequest req
    ) {
        return employerService.updateCurrentEmployer(authentication.getName(), req);
    }

    @PostMapping("/profile/picture")
    public String uploadPicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        String email = authentication.getName();
        return employerService.uploadProfilePicture(email, file);
    }
    
	@GetMapping("/resume/{applicationId}")
	public ResponseEntity<Resource> downloadApplicantResume(@PathVariable Long applicationId, Principal principal)
			throws IOException {
		return employerService.downloadResumeForEmployer(applicationId, principal.getName());
	}


	@GetMapping("/applications/{jobId}")
	public ResponseEntity<List<ApplicationResponseDto>> getApplicantsForJob(@PathVariable Long jobId,
			Authentication authentication) {

		String employerEmail = authentication.getName();
		List<ApplicationResponseDto> applications = employerService.getApplicationsForJob(employerEmail, jobId);
		return ResponseEntity.ok(applications);
	}
	
    @GetMapping("/applications")
    public ResponseEntity<List<EmployerCandidateViewDto>> getAllApplications(Authentication authentication) {
        String employerEmail = authentication.getName();
        List<EmployerCandidateViewDto> applications = employerService.getAllApplications(employerEmail);
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/applications/details/{id}")
    public ResponseEntity<EmployerCandidateViewDto> getApplicationDetail(
            @PathVariable Long id) {
        EmployerCandidateViewDto detail = employerService.getApplicationDetail(id);
        return ResponseEntity.ok(detail);
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        String employerEmail = authentication.getName();
        employerService.updateApplicationStatus(id, status, employerEmail);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/applications/{id}/interview")
    public ResponseEntity<Void> scheduleInterview(
            @PathVariable Long id,
            @RequestBody InterviewRequest request,
            Authentication authentication) {

        String employerEmail = authentication.getName();
        interviewService.scheduleInterview(id, request, employerEmail);
        return ResponseEntity.ok().build();
    }


   

}
