package cs.job.recruit.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.job.recruit.api.input.JobRequest;
import cs.job.recruit.api.input.JobSearch;
import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.api.output.JobDetailsResponse;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.service.JobManagementService;

@RestController
@RequestMapping("/jobs")
public class JobController {

	@Autowired
	private JobManagementService service;

	@GetMapping("all")
	PageResult<JobDetailsResponse> search(@ModelAttribute JobSearch search,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size, Authentication authentication) {
		
		if (authentication != null) {
			String email = authentication.getName();
			return service.searchWithApplyStatus(search, page, size, email);
		}
		return service.searchWithApplyStatus(search, page, size, null);

	}

	@GetMapping("/salaries")
	PageResult<JobDetails> searchByMaxSalaries(JobSearch search,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		var result = service.searchByMaxSalary(search, page, size);
		return result;

	}

	@PostMapping("/post/create")
	public ResponseEntity<?> createJob(@Validated @RequestBody JobRequest request, BindingResult result) {
		return service.create(request);
	}

	@GetMapping("/company")
	public PageResult<JobDetails> getMyJobs(JobSearch search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Authentication authentication) {
		String employerEmail = authentication.getName();

		PageResult<JobDetails> result = service.searchByEmployer(search, employerEmail, page, size);
		return result;
	}

	@GetMapping("/company/{id}/posted")
	public PageResult<JobDetails> getJobsByCompany(@PathVariable UUID id, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		var result = service.getJobsByCompany(id, page, size);
		return result;
	}

	@GetMapping("{id}")
	public JobDetails searchJobById(@PathVariable long id) {
		return service.getJobDetails(id);
	}

}
