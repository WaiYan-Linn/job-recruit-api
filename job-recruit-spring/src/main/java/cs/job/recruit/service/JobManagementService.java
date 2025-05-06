package cs.job.recruit.service;

import java.time.LocalDate;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cs.job.recruit.api.input.JobRequest;
import cs.job.recruit.api.input.JobSearch;
import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.respository.JobRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
			cq.where(search.where(cb, root));

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

}
