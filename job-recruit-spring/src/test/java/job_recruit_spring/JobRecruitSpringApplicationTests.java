package job_recruit_spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import cs.job.recruit.JobRecruitSpringApplication;

@SpringBootTest(classes = JobRecruitSpringApplication.class) // explicitly point to main app config
class JobRecruitSpringApplicationTests {

	
	@Autowired
	PasswordEncoder passwordEncoder;
	@Test
	void contextLoads() {
		System.out.println(passwordEncoder.encode("111111"));
		System.out.println(passwordEncoder.encode("222222"));

	}

}
