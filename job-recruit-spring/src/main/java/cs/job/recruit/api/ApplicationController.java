package cs.job.recruit.api;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.job.recruit.api.input.InterviewRequest;
import cs.job.recruit.api.output.ApplicationResponseDto;
import cs.job.recruit.api.output.EmployerCandidateViewDto;
import cs.job.recruit.domain.entity.Application.Status;
import cs.job.recruit.service.ApplicationService;
import cs.job.recruit.service.EmployerService;
import cs.job.recruit.service.InterviewService;

@RestController
@RequestMapping("applications")
public class ApplicationController {

	@Autowired
	private EmployerService employerService;

	@Autowired
	private InterviewService interviewService;

	@Autowired
	private ApplicationService applicationService;

	@GetMapping("/resume/{applicationId}")
	public ResponseEntity<Resource> downloadApplicantResume(@PathVariable Long applicationId, Principal principal)
			throws IOException {
		return employerService.downloadResumeForEmployer(applicationId, principal.getName());
	}

	@GetMapping("/{jobId}")
	public ResponseEntity<List<ApplicationResponseDto>> getApplicantsForJob(@PathVariable Long jobId,
			Authentication authentication) {

		String employerEmail = authentication.getName();
		List<ApplicationResponseDto> applications = employerService.getApplicationsForJob(employerEmail, jobId);
		return ResponseEntity.ok(applications);
	}

	@GetMapping
	public ResponseEntity<List<EmployerCandidateViewDto>> getAllApplications(Authentication authentication) {
		String employerEmail = authentication.getName();
		List<EmployerCandidateViewDto> applications = employerService.getAllApplications(employerEmail);
		return ResponseEntity.ok(applications);
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<EmployerCandidateViewDto> getApplicationDetail(@PathVariable Long id) {
		EmployerCandidateViewDto detail = employerService.getApplicationDetail(id);
		return ResponseEntity.ok(detail);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> updateApplicationStatus(@PathVariable Long id, @RequestParam String status,
			Authentication authentication) {
		String employerEmail = authentication.getName();
		employerService.updateApplicationStatus(id, status, employerEmail);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/interview")
	public ResponseEntity<Void> scheduleInterview(@PathVariable Long id, @RequestBody InterviewRequest request,
			Authentication authentication) {

		String employerEmail = authentication.getName();
		interviewService.scheduleInterview(id, request, employerEmail);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{id}/interview")
	public ResponseEntity<InterviewRequest> getInterviewInfo(@PathVariable Long id){
		InterviewRequest detail = interviewService.getInterviewInfo(id);
		return ResponseEntity.ok(detail);
	}

	@PostMapping("/{id}/hired")
	public ResponseEntity<Void> hire(@PathVariable Long id, Authentication authentication) {

		String employerEmail = authentication.getName();
		interviewService.hired(id, employerEmail);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/rejected")
	public ResponseEntity<Void> reject(@PathVariable Long id, Authentication authentication) {

		String employerEmail = authentication.getName();
		interviewService.reject(id, employerEmail);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/job")
	public ResponseEntity<List<EmployerCandidateViewDto>> getApplicationsByJob(@RequestParam Long jobId) {
		List<EmployerCandidateViewDto> applications = applicationService.getApplicationsByJob(jobId);
		return ResponseEntity.ok(applications);
	}

	@GetMapping("/status")
	public ResponseEntity<List<EmployerCandidateViewDto>> getApplicationsByStatus(@RequestParam String status) {
		try {
			Status applicationStatus = Status.valueOf(status.toUpperCase());
			List<EmployerCandidateViewDto> applications = applicationService.getApplicationsByStatus(applicationStatus);
			return ResponseEntity.ok(applications);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
