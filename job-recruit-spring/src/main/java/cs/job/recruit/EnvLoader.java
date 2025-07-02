package cs.job.recruit;

import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvLoader {

	@PostConstruct
	public void load() {
	    System.out.println("✅ EnvLoader is running...");

	    Dotenv dotenv = Dotenv.configure()
	        .ignoreIfMissing()
	        .load();

	    String mailUsername = dotenv.get("MAIL_USERNAME");
	    String mailPassword = dotenv.get("MAIL_PASSWORD");
	    
	    

	    if (mailUsername != null) {
	        System.setProperty("MAIL_USERNAME", mailUsername);
	    }
	    if (mailPassword != null) {
	        System.setProperty("MAIL_PASSWORD", mailPassword);
	    }
	    
	    System.out.println(mailUsername+ mailPassword);

	    if (mailUsername == null) {
	        System.err.println("❌ MAIL_USERNAME is missing in .env");
	    }
	    if (mailPassword == null) {
	        System.err.println("❌ MAIL_PASSWORD is missing in .env");
	    } else {
	        System.out.println("✅ MAIL_USERNAME and MAIL_PASSWORD loaded");
	    }
	}

}
