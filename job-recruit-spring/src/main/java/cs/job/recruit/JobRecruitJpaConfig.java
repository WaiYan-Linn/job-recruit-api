package cs.job.recruit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import cs.job.recruit.domain.BaseRepositoryImpl;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
	    basePackages        = "cs.job.recruit.domain",   // ← where your JobRepository lives
	    repositoryBaseClass = BaseRepositoryImpl.class
	)
public class JobRecruitJpaConfig {

}
