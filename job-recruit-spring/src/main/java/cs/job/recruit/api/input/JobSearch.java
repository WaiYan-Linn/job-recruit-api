package cs.job.recruit.api.input;

import java.util.ArrayList;
import java.util.UUID;

import cs.job.recruit.domain.entity.Employer_;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.Job.Category;
import cs.job.recruit.domain.entity.Job_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public record JobSearch(String specialization,String keyword, String location,  UUID employerId) {

    public Predicate[] where(CriteriaBuilder cb, Root<Job> root) {
        var params = new ArrayList<Predicate>();

        if (specialization != null && !specialization.isBlank()) {
            params.add(cb.equal(root.get(Job_.category), Category.valueOf(specialization)));
        }

        if (location != null && !location.isBlank()) {
            params.add(cb.like(cb.lower(root.get(Job_.location)), "%" + location.toLowerCase() + "%"));
        }

        if (keyword != null && !keyword.isBlank()) {
            String pattern = "%" + keyword.toLowerCase() + "%";
            Predicate titleLike = cb.like(cb.lower(root.get(Job_.title)), pattern);
            Predicate descLike = cb.like(cb.lower(root.get(Job_.description)), pattern);
            Predicate reqLike = cb.like(cb.lower(root.get(Job_.requirements)), pattern);
            params.add(cb.or(titleLike, descLike, reqLike));
        }

        if (employerId != null) {
            params.add(cb.equal(root.get(Job_.employer).get(Employer_.id), employerId));
        }
        


        return params.toArray(new Predicate[0]);
    }
}
