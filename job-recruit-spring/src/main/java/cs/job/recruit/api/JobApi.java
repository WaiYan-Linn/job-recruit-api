package cs.job.recruit.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.job.recruit.api.input.JobRequest;
import cs.job.recruit.api.input.JobSearch;
import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.service.JobManagementService;

@RestController
@RequestMapping("jobs")
public class JobApi {

	@Autowired
	private JobManagementService service;
	
	@GetMapping
	PageResult<JobDetails> search(JobSearch search,
			@RequestParam(required = false, defaultValue = "0") int page, 
			@RequestParam(required = false, defaultValue = "10") int size) {
		return service.search(search, page, size);
		
	}
	
	@PreAuthorize("hasRole('EMPLOYER')")
	@PostMapping
	public ResponseEntity<?> createJob(@Validated @RequestBody JobRequest request,
			 BindingResult result) {
	   return service.create(request);
	}

}
