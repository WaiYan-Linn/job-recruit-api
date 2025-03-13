package cs.job.recruit.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cs.job.recruit.domain.entity.OTP;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmail(String email);
    void deleteByEmail(String email);
}