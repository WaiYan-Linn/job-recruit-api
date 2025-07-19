package cs.job.recruit.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cs.job.recruit.api.output.EmployerDetails;
import cs.job.recruit.api.output.JobDetails;
import cs.job.recruit.domain.BaseRepository;
import cs.job.recruit.domain.entity.Account;
import cs.job.recruit.domain.entity.Employer;

public interface EmployerRepo extends BaseRepository<Employer, UUID>{

	Optional<Employer> findOneByAccountEmail(String username);

	@Query("SELECT new cs.job.recruit.api.output.EmployerDetails(e.id, e.companyName, e.website, e.profilePictureUrl, e.aboutUs, a.address, a.phone) FROM Employer e JOIN e.account a")
	Page<EmployerDetails> findAllEmployerDetails(Pageable pageable);

	@Query("SELECT new cs.job.recruit.api.output.EmployerDetails(e.id, e.companyName, e.website, e.profilePictureUrl, e.aboutUs, a.address, a.phone) FROM Employer e JOIN e.account a WHERE LOWER(e.companyName) LIKE LOWER(CONCAT('%', :name, '%'))")
	Page<EmployerDetails> findByCompanyNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

	Optional<Employer> findOneByAccount(Account account);

}
