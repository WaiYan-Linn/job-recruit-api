package cs.job.recruit.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.job.recruit.domain.entity.OTP;
import cs.job.recruit.respository.OTPRepository;
import jakarta.transaction.Transactional;

@Service
public class OTPService {
	@Autowired
	private OTPRepository otpRepo;

	public String generateOTP() {
		return String.format("%06d", new Random().nextInt(999999));
	}

	@Transactional
	public void saveOTP(String email, String otp) {
		otpRepo.deleteByEmail(email);
		OTP newOtp = new OTP();
		newOtp.setEmail(email);
		newOtp.setCode(otp);
		newOtp.setExpiryTime(LocalDateTime.now().plusMinutes(2));
		otpRepo.save(newOtp);
	}

	public boolean validateOTP(String email, String otp) {
		Optional<OTP> otpEntity = otpRepo.findByEmail(email);
		return otpEntity.isPresent() && otpEntity.get().getCode().equals(otp)
				&& otpEntity.get().getExpiryTime().isAfter(LocalDateTime.now());
	}
}