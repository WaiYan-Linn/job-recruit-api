package cs.job.recruit;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class EnvLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
    	Dotenv dotenv = Dotenv.configure()
    		    .filename(".env")
    		    .ignoreIfMissing()
    		    .load();

        Map<String, Object> envMap = new HashMap<>();
        String username = dotenv.get("MAIL_USERNAME");
        String password = dotenv.get("MAIL_PASSWORD");

        if (username != null) envMap.put("USERNAME", username);
        if (password != null) envMap.put("PASSWORD", password);

        System.out.println("USERNAME = " + username);
        System.out.println("USERNAME length = " + username.length());

        ConfigurableEnvironment environment = context.getEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));

        System.out.println("[DotenvInitializer] Loaded USERNAME and PASSWORD from .env"
        		+ username+ password);
    }
}
