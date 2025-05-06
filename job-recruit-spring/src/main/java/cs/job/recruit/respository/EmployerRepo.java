package cs.job.recruit.respository;

import java.util.Optional;
import java.util.UUID;

import cs.job.recruit.domain.BaseRepository;
import cs.job.recruit.domain.entity.Employer;

public interface EmployerRepo extends BaseRepository<Employer, UUID>{

	Optional<Employer> findOneByAccountEmail(String username);
}
