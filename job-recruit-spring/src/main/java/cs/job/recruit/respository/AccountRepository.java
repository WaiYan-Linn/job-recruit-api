package cs.job.recruit.respository;

import java.util.Optional;
import java.util.UUID;

import cs.job.recruit.domain.BaseRepository;
import cs.job.recruit.domain.entity.Account;

public interface AccountRepository extends BaseRepository<Account, UUID>{

	Optional<Account> findOneByEmail(String email);
}
