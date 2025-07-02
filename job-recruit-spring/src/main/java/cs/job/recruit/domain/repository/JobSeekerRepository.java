package cs.job.recruit.domain.repository;

import java.util.Optional;
import java.util.UUID;

import cs.job.recruit.domain.BaseRepository;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.JobSeeker;

public interface JobSeekerRepository  extends BaseRepository<JobSeeker, UUID> {

	Optional<JobSeeker> findByAccountId(UUID accountId);

	Optional<JobSeeker> findOneByAccountEmail(String email);


}
