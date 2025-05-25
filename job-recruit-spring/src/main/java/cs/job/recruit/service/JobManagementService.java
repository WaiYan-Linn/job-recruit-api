package cs.job.recruit.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cs.job.recruit.api.input.JobRequest;
import cs.job.recruit.api.input.JobSearch;
import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.repository.JobRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobManagementService {

	private final JobRepository jobRepository;
	private final LoginMemberService loginMemberService;

	public PageResult<JobDetails> search(JobSearch search, int page, int size) {
		return jobRepository.search(queryFunc(search), countFunc(search), page, size);
	}

	private Function<CriteriaBuilder, CriteriaQuery<JobDetails>> queryFunc(JobSearch search) {
		return cb -> {
			var cq = cb.createQuery(JobDetails.class);
			var root = cq.from(Job.class);
			JobDetails.select(cb, cq, root);
			cq.where(search.where(cb, root))
			.orderBy(cb.desc(root.get("postedAt")));

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
			cq.where(all);

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


}
