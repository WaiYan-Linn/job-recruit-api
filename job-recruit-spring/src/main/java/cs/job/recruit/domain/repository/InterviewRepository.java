package cs.job.recruit.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs.job.recruit.domain.entity.Interview;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

}
