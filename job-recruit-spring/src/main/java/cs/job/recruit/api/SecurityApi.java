package cs.job.recruit.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.job.recruit.api.input.RefreshForm;
import cs.job.recruit.api.input.SignInForm;
import cs.job.recruit.api.input.SignUpForm;
import cs.job.recruit.api.output.AccountInfo;
import cs.job.recruit.service.EmailService;
import cs.job.recruit.service.MemberSignUpService;
import cs.job.recruit.service.OTPService;
import cs.job.recruit.service.TokenManagementService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("security")
public class SecurityApi {

	private final TokenManagementService tokenService;
	private final MemberSignUpService signUpService;
	private final OTPService otpService;
	private final EmailService emailService;

	@PostMapping("signin")
	AccountInfo signIn(@Validated @RequestBody SignInForm form, BindingResult result) {
		return tokenService.generate(form);
	}

	@PostMapping("refresh")
	public ResponseEntity<?> refreshToken(@Validated @RequestBody RefreshForm form, BindingResult result) {
	    if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body("Invalid request format");
	    }
	    try {
	        AccountInfo accountInfo = tokenService.refresh(form);
	        return ResponseEntity.ok(accountInfo);
	    
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
	    }
	}


	@PostMapping("signup")
	AccountInfo signUp(@Validated @RequestBody SignUpForm form,@RequestParam String code, BindingResult result) {
		
		
	
		if (otpService.validateOTP(form.email(), code)) {
			signUpService.signUp(form);
	}
		return tokenService.generate(new SignInForm(form.email(), form.password()));		
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@Validated @RequestBody SignUpForm form, BindingResult result) {
		
		String otp = otpService.generateOTP();
		otpService.saveOTP(form.email(), otp);
		emailService.sendOTP(form.email(), otp);
		
		
		return ResponseEntity.ok("OTP sent");


	}
}
