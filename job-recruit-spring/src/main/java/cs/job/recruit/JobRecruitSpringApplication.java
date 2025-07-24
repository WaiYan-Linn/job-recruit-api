package cs.job.recruit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class JobRecruitSpringApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // Loads .env from current directory
		System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		// ... set other system properties based on .env variables
		SpringApplication.run(JobRecruitSpringApplication.class, args);

	}

}
