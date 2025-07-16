package cs.job.recruit.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cs.job.recruit.api.input.JobRequest;
import cs.job.recruit.api.input.JobSearch;
import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.api.output.JobDetailsResponse;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.JobRepository;
import cs.job.recruit.domain.repository.JobSeekerRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobManagementService {

	private final JobRepository jobRepository;
	private final LoginMemberService loginMemberService;
	private final JobSeekerRepository jobSeekerRepository;
	private final ApplicationRepository applicationRepository;

	public PageResult<JobDetails> search(JobSearch search, int page, int size) {
		return jobRepository.search(queryFunc(search), countFunc(search), page, size);
	}

	private Function<CriteriaBuilder, CriteriaQuery<JobDetails>> queryFunc(JobSearch search) {
		return cb -> {
			var cq = cb.createQuery(JobDetails.class);
			var root = cq.from(Job.class);
			JobDetails.select(cb, cq, root);
			cq.where(search.where(cb, root)).orderBy(cb.desc(root.get("postedAt")));
			
			

			return cq;
		};
	}

	private Function<CriteriaBuilder, CriteriaQuery<Long>> countFunc(JobSearch search) {
		return cb -> {
			var cq = cb.createQuery(Long.class);
			var root = cq.from(Job.class);

			cq.select(cb.count(root));
			cq.where(search.where(cb, root));
			return cq;
		};
	}

	public ResponseEntity<?> create(JobRequest request) {
		var employer = loginMemberService.getLoginUser();
		var entity = request.entity();
		entity.setEmployer(employer);
		entity.setPostedAt(LocalDate.now()); // optional: set post date
		var saved = jobRepository.save(entity);

		return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId()); // or a DTO
	}

	public JobDetails getJobDetails(Long jobId) {
		return jobRepository.findJobDetailsById(jobId).orElseThrow(() -> new RuntimeErrorException(null));
	}

	public PageResult<JobDetails> searchByEmployer(JobSearch search, String employerEmail, int page, int size) {
		return jobRepository.search(queryFuncWithEmployer(search, employerEmail),
				countFuncWithEmployer(search, employerEmail), page, size);
	}

	private Function<CriteriaBuilder, CriteriaQuery<JobDetails>> queryFuncWithEmployer(JobSearch search, String email) {
		return cb -> {
			var cq = cb.createQuery(JobDetails.class);
			var root = cq.from(Job.class);

			JobDetails.select(cb, cq, root);

			// 1) get your original predicates array
			Predicate[] orig = search.where(cb, root);

			Predicate byEmployer = cb.equal(root.get("employer") // from Job → Employer
					.get("account") // from Employer → Account
					.get("email"), // finally the email field
					email);
			// 3) make a new array one slot bigger
			Predicate[] all = Arrays.copyOf(orig, orig.length + 1);
			all[orig.length] = byEmployer;

			// 4) pass the combined array to where()
			cq.where(all).orderBy(cb.desc(root.get("postedAt")));

			return cq;
		};
	}

	private Function<CriteriaBuilder, CriteriaQuery<Long>> countFuncWithEmployer(JobSearch search, String email) {
		return cb -> {
			var cq = cb.createQuery(Long.class);
			var root = cq.from(Job.class);

			cq.select(cb.count(root));

			Predicate[] orig = search.where(cb, root);
			Predicate byEmployer = cb.equal(root.get("employer") // from Job → Employer
					.get("account") // from Employer → Account
					.get("email"), // finally the email field
					email);
			Predicate[] all = Arrays.copyOf(orig, orig.length + 1);
			all[orig.length] = byEmployer;

			cq.where(all);
			return cq;
		};
	}

	public PageResult<JobDetails> getJobsByCompany(UUID employerId, int page, int size) {
		JobSearch search = new JobSearch(null, null, null, employerId); // Only filter by employerId
		return this.search(search, page, size); // Reuses your existing search method
	}

	public PageResult<JobDetails> searchByMaxSalary(JobSearch search, int page, int size) {
		return jobRepository.search(maxSalaryQueryFunc(search), countFunc(search), page, size);
	}

	private Function<CriteriaBuilder, CriteriaQuery<JobDetails>> maxSalaryQueryFunc(JobSearch search) {
		return cb -> {
			var cq = cb.createQuery(JobDetails.class);
			var root = cq.from(Job.class);
			JobDetails.select(cb, cq, root);

		

			// Create deadline predicate
			Predicate deadlinePredicate = cb.greaterThanOrEqualTo(root.get("deadline"), LocalDate.now());

			
			// Apply where clause
			cq.where(cb.and(deadlinePredicate)).orderBy(cb.desc(root.get("salaryMax")));

			return cq;
		};
	}

	public PageResult<JobDetailsResponse> searchWithApplyStatus(JobSearch search, int page, int size, String email) {
	    // 1. Perform normal search
	    PageResult<JobDetails> jobPage = search(search, page, size);
	    
	    if (email == null) {
	        List<JobDetailsResponse> defaultList = jobPage.contents().stream()
	            .map(job -> new JobDetailsResponse(job, false))
	            .toList();

	        return new PageResult<>(
	            defaultList,
	            jobPage.totalItems(),
	            jobPage.size(),
	            jobPage.currentPage()
	        );
	    }

	    // 2. Get job seeker
	    JobSeeker seeker = jobSeekerRepository
	        .findOneByAccountEmail(email)
	        .orElseThrow(() -> new IllegalStateException("JobSeeker not found"));

	    // 3. Extract job IDs
	    List<Long> jobIds = jobPage.contents().stream()
	        .map(JobDetails::id)
	        .toList();

	    // 4. Find applications by seeker and job IDs
	    List<Application> applications = applicationRepository
	        .findByJobSeekerAndJobIdIn(seeker, jobIds);

	    Set<Long> appliedJobIds = applications.stream()
	        .map(app -> app.getJob().getId())
	        .collect(Collectors.toSet());

	    // 5. Wrap with hasApplied
	    List<JobDetailsResponse> wrappedList = jobPage.contents().stream()
	        .map(job -> new JobDetailsResponse(job, appliedJobIds.contains(job.id())))
	        .toList();

	    return new PageResult<>(wrappedList,jobPage.totalItems() ,jobPage.size(), jobPage.currentPage());
	}


}
